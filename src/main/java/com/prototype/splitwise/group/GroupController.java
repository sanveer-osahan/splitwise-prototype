package com.prototype.splitwise.group;

import com.prototype.splitwise.entity.EntityCrudController;
import com.prototype.splitwise.entity.EntityService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = Group.SERVICE, produces = MediaType.APPLICATION_JSON_VALUE)
public class GroupController extends EntityCrudController<Group> {

    protected GroupController(EntityService<Group> entityService) {
        super(entityService);
    }
}
