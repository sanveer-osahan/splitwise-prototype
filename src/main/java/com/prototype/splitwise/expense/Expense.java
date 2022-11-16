package com.prototype.splitwise.expense;

import com.prototype.splitwise.entity.Entity;
import com.prototype.splitwise.entity.IDNameReference;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Set;

@Getter
@Setter
public class Expense extends Entity<Expense.Data> {

    public static final String ENTITY_TYPE = "expense";
    public static final String SERVICE = ENTITY_TYPE + "s";

    @Getter
    @Setter
    public static class Data {

        @NotNull private SplitType splitType = SplitType.EQUAL;

        @NotNull @Positive private Double totalAmount;

        // The user who paid the amount
        @Valid private IDNameReference owner;

        // Determines if the owner's contribution should be counted in the split
        private boolean isOwnerIncluded = true;

        @Valid @NotEmpty private Set<Split> splits;

        @NotNull @Valid private IDNameReference group;

        // Determines if at least one of the users have settled their amount
        private boolean isPartialSettled;

        private boolean isFullSettled;
    }
}
