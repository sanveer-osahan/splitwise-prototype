package com.prototype.splitwise.expense;

import com.prototype.splitwise.entity.EntityRepository;
import org.springframework.stereotype.Repository;

@Repository
interface ExpenseRepository extends EntityRepository<Expense> {
}
