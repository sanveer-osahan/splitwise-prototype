package com.prototype.splitwise.expense;

import com.prototype.splitwise.entity.IDNameReference;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Getter
@Setter
public class Split {

    @Valid @NotNull private IDNameReference user;
    private Double amount;

    private Split() {}

    private Split(IDNameReference user, Double amount) {
        this.user = user;
        this.amount = amount;
    }

    public static Split as(IDNameReference user) {
        return new Split(user, null);
    }

    public static Split as(IDNameReference user, Double amount) {
        return new Split(user, amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Split split = (Split) o;
        return user.equals(split.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }
}
