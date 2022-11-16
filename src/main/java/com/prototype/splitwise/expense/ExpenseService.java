package com.prototype.splitwise.expense;

import com.prototype.splitwise.config.AuthContext;
import com.prototype.splitwise.entity.EntityService;
import com.prototype.splitwise.entity.IDNameReference;
import com.prototype.splitwise.entity.PaginationRequest;
import com.prototype.splitwise.event.Event;
import com.prototype.splitwise.exception.ClientException;
import com.prototype.splitwise.group.Group;
import com.prototype.splitwise.user.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
final class ExpenseService extends EntityService<Expense> {

    private final EntityService<User> userService;
    private final EntityService<Group> groupService;
    private final ExpenseRepository expenseRepository;

    private ExpenseService(
            ExpenseRepository expenseRepository,
            EntityService<User> userService,
            EntityService<Group> groupService,
            KafkaTemplate<String, Event<Expense>> kafkaTemplate) {
        super(expenseRepository, kafkaTemplate);
        this.userService = userService;
        this.expenseRepository = expenseRepository;
        this.groupService = groupService;
    }

    @Override
    public Page<Expense> getPaginatedResponse(PaginationRequest paginationRequest) {
        return expenseRepository.findByUserIdAndCreatedBetween(
                AuthContext.getCurrentUserOrElseThrow(),
                paginationRequest.getFromTime(),
                paginationRequest.getToTime(),
                paginationRequest.getPageable());
    }

    @Override
    protected void beforeCreate(Expense resource) {
        super.beforeCreate(resource);
        splitExpenses(resource);
    }

    @Override
    protected void beforeUpdate(Expense oldResource, Expense newResource) {
        super.beforeUpdate(oldResource, newResource);
        if (!oldResource.getData().getSettlement().isPending()) {
            throw new ClientException("Cannot update when at least one user has settled the amount. " +
                    "Kindly create new expenses for the same");
        }
        splitExpenses(newResource);
    }

    @Override
    protected void beforeDelete(Expense resource) {
        super.beforeDelete(resource);
        if (!resource.getData().getSettlement().isPending()) {
            throw new ClientException("Cannot delete when at least one user has settled the amount.");
        }
    }

    private void splitExpenses(Expense expense) {
        var expenseData = expense.getData();

        // Validate Group
        var group = groupService.getEntity(expenseData.getGroup().getId());
        expenseData.getGroup().setName(group.getMeta().getName());

        validateExpenseOwner(expense, group);

        expenseData.getSplits().stream()
                .map(Split::getUser)
                .forEach(userRef -> validateSplitUser(userRef, group));

        expenseData.setSettlement(State.PENDING);

        switch (expense.getData().getSplitType()) {
            case EQUAL:
                equalSplit(expense);
                break;
            case EXACT:
                exactSplit(expense);
                break;
        }
    }

    private void validateExpenseOwner(Expense expense, Group group) {
        var expenseData = expense.getData();
        var ownerRef = expenseData.getOwner();
        if (Objects.isNull(ownerRef) || StringUtils.isBlank(ownerRef.getId())) {
            expenseData.setOwner(IDNameReference.of(AuthContext.getCurrentUserOrElseThrow(), null));
        }
        var owner = userService.getEntity(expenseData.getOwner().getId());
        expenseData.getOwner().setName(owner.getMeta().getName());
    }

    // Validate split users exist and whether they are part of the group
    private void validateSplitUser(IDNameReference userRef, Group group) {
        var user = userService.getEntity(userRef.getId());
        if (!group.getData().getUsers().contains(userRef)) {
            throw new ClientException(
                    user.getId() + " doesn't belong to the group " + group.getId());
        }
        userRef.setName(user.getMeta().getName());
    }

    /**
     * The total amount should not be less than the total split amount. <br>
     * Include the owner in the split if there's a difference in both the amounts, assuming it to be owner's
     * expected contribution in the expense
     * @param expense The expense splits
     */
    private void exactSplit(Expense expense) {
        var expenseData = expense.getData();
        var splitUsers = expenseData.getSplits().stream()
                .collect(Collectors.toMap(Split::getUser, Function.identity()));
        var owner = expenseData.getOwner();
        if (splitUsers.containsKey(owner)) {
            expenseData.getSplits().remove(splitUsers.get(owner));
        }

        double totalSplitAmount = expenseData.getSplits().stream()
                .map(split -> {
                    validateSplitAmount(split);
                    return split.getAmount();
                })
                .reduce(0D, Double::sum);

        double difference = expenseData.getTotalAmount() - totalSplitAmount;

        if (difference < 0) {
            throw new ClientException("The total amount entered cannot be less than the split amounts total");
        }
        if (difference == 0) {
            expenseData.setExcludeOwner(true);
        } else {
            expenseData.getSplits().add(Split.as(owner, difference));
            expenseData.setExcludeOwner(false);
        }
    }

    private void validateSplitAmount(Split split) {
        if (Objects.isNull(split.getAmount()) || split.getAmount() <= 0) {
            throw new ClientException("Invalid or no split amount entered for user : " + split.getUser().getId());
        }
    }

    private void equalSplit(Expense expense) {
        var expenseData = expense.getData();
        var splits = expenseData.getSplits();
        if (Objects.isNull(expenseData.getExcludeOwner()) || Boolean.FALSE.equals(expenseData.getExcludeOwner())) {
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
