package com.prototype.splitwise.expense;

import com.prototype.splitwise.entity.Entity;
import com.prototype.splitwise.entity.IDNameReference;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Set;

@CompoundIndexes({
        @CompoundIndex(name = "split_users_index", def = "{ 'data.splits.user._id' : 1, 'data.owner._id' : 1, 'meta.audit.createdOn' : -1 }")
})
@Document(Expense.SERVICE)
@Getter
@Setter
public class Expense extends Entity<Expense.ExpenseData> {

    public static final String ENTITY_TYPE = "expense";
    public static final String SERVICE = ENTITY_TYPE + "s";

    @Getter
    @Setter
    public static class ExpenseData {

        @NotNull private SplitType splitType = SplitType.EQUAL;

        @NotNull @Positive private Double totalAmount;

        // The user who paid the amount
        @Valid private IDNameReference owner;

        // Determines if the owner's contribution should be counted in the split
        private Boolean excludeOwner;

        @Valid @NotEmpty private Set<Split> splits;

        @NotNull @Valid private IDNameReference group;

        private State settlement = State.PENDING;
    }
}
