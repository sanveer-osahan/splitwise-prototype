package com.prototype.splitwise.expense;

public enum State {

    PENDING,
    PARTIAL,
    COMPLETE;

    public boolean isPending() {
        return PENDING.equals(this);
    }
}
