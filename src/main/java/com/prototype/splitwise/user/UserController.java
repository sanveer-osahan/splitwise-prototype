package com.prototype.splitwise.user;

import com.prototype.splitwise.entity.EntityCrudController;
import com.prototype.splitwise.entity.EntityService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = User.SERVICE, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController extends EntityCrudController<User> {

    protected UserController(EntityService<User> userService) {
        super(userService);
    }
}
