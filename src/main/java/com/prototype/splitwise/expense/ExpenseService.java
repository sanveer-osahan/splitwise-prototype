package com.prototype.splitwise.expense;

import com.prototype.splitwise.entity.EntityRepository;
import com.prototype.splitwise.entity.EntityService;
import com.prototype.splitwise.entity.IDNameReference;
import com.prototype.splitwise.event.Event;
import com.prototype.splitwise.exception.ClientException;
import com.prototype.splitwise.user.User;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
final class ExpenseService extends EntityService<Expense> {

    private final EntityService<User> userService;

    private ExpenseService(
            EntityRepository<Expense> expenseRepository,
            EntityService<User> userService,
            KafkaTemplate<String, Event<Expense>> kafkaTemplate) {
        super(expenseRepository, kafkaTemplate);
        this.userService = userService;
    }

    @Override
    protected void beforeCreate(Expense resource) {
        super.beforeCreate(resource);
        splitExpenses(resource);
    }

    @Override
    protected void beforeUpdate(Expense oldResource, Expense newResource) {
        super.beforeUpdate(oldResource, newResource);
        if (oldResource.getData().isFullSettled() || oldResource.getData().isPartialSettled()) {
            throw new ClientException("Cannot update when at least one user has settled the amount. " +
                    "Kindly create new expenses for the same");
        }
        splitExpenses(newResource);
    }

    private void splitExpenses(Expense expense) {
        // Validate owner
        userService.getEntity(expense.getData().getOwner().getId());

        // Validate split users
        expense.getData().getSplits()
                .stream()
                .map(Split::getUser)
                .map(IDNameReference::getId)
                .forEach(userService::getEntity);

        expense.getData().setFullSettled(false);
        expense.getData().setPartialSettled(false);

        switch (expense.getData().getSplitType()) {
            case EQUAL:
                equalSplit(expense);
                break;
            case EXACT:
                exactSplit(expense);
                break;
        }
    }

    /**
     * The total amount should not be less than the total split amount. <br>
     * Include the owner in the split if there's a difference in both the amounts, assuming it to be owner's
     * expected contribution in the expense
     * @param expense The expense splits
     */
    private void exactSplit(Expense expense) {
        var expenseData = expense.getData();
        double totalSplitAmount = expenseData.getSplits().stream()
                .map(Split::getAmount)
                .reduce(0D, Double::sum);
        var splitUsersStream = expenseData.getSplits().stream().map(Split::getUser);
        double difference = expenseData.getTotalAmount() - totalSplitAmount;
        if (difference > 0) {
            throw new ClientException("The total amount entered cannot be less than the split amounts");
        }
        if (difference == 0
                && splitUsersStream.noneMatch(user -> user.equals(expenseData.getOwner()))) {
            expenseData.setOwnerIncluded(false);
        } else {
            expenseData.setOwnerIncluded(true);
            expenseData.getSplits().add(Split.as(expenseData.getOwner(), difference));
        }
    }

    private void equalSplit(Expense expense) {
        var expenseData = expense.getData();
        var splits = expenseData.getSplits();
        if (expenseData.isOwnerIncluded()) {
            splits.add(Split.as(expenseData.getOwner()));
        }
        double splitAmount = expenseData.getTotalAmount() / splits.size();
        expenseData.getSplits().forEach(split -> split.setAmount(splitAmount));
    }

    @Override
    protected String getEntityType() {
        return Expense.ENTITY_TYPE;
    }
}
