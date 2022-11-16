package com.prototype.splitwise.expense;

import com.prototype.splitwise.entity.EntityController;
import com.prototype.splitwise.entity.EntityService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = Expense.SERVICE, produces = MediaType.APPLICATION_JSON_VALUE)
public class ExpenseController extends EntityController<Expense> {

    protected ExpenseController(EntityService<Expense> entityService) {
        super(entityService);
    }
}
