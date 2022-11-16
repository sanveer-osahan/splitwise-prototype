package com.prototype.splitwise.settlement;

import com.prototype.splitwise.entity.EntityEventListener;
import com.prototype.splitwise.entity.IDNameReference;
import com.prototype.splitwise.event.Event;
import com.prototype.splitwise.expense.Expense;
import com.prototype.splitwise.expense.Split;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * This service will listen to the expense crud events and create, update the settlements on each user level
 */
@KafkaListener(
        topics = "crud_expense",
        groupId = "expense_settlement_crud"
)
@Component
public class ExpenseSettlementCrudListener extends EntityEventListener<Expense> {

    private final SettlementService settlementService;

    public ExpenseSettlementCrudListener(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @Override
    protected void onCreate(Event<Expense> event) {
        var expense = event.getSource();
        var splits = expense.getData().getSplits();
        var splitUsers = splits.stream()
                .map(Split::getUser)
                .collect(Collectors.toSet());
        splits.stream()
                .map(split -> createSettlement(expense, split))
                .forEach(settlementService::createEntity);
        if (!splitUsers.contains(expense.getData().getOwner())) {
            var ownerSettlement = createSettlement(expense, Split.as(expense.getData().getOwner(), 0D));
            settlementService.createEntity(ownerSettlement);
        }
    }

    @Override
    protected void onUpdate(Event<Expense> event) {
        onDelete(event);
        onCreate(event);
    }

    @Override
    protected void onDelete(Event<Expense> event) {
        var expense = event.getSource();
        settlementService.getSettlements(expense.getId()).forEach(settlementService::deleteEntity);
    }

    private Settlement createSettlement(Expense expense, Split split) {
        var expenseData = expense.getData();
        double pendingAmount = split.getAmount() * -1;
        if (split.getUser().equals(expenseData.getOwner())) {
            pendingAmount = expenseData.getTotalAmount() - split.getAmount();
        }
        var settlementData = Settlement.SettlementData.builder()
                .owner(expenseData.getOwner())
                .expense(IDNameReference.of(expense))
                .settler(split.getUser())
                .pendingAmount(pendingAmount)
                .totalAmount(expenseData.getTotalAmount())
                .group(expenseData.getGroup())
                .build();

        Settlement settlement = new Settlement();
        settlement.setMeta(expense.getMeta());
        settlement.setData(settlementData);
        return settlement;
    }
}
