package com.prototype.splitwise.user;

import com.prototype.splitwise.config.MongoComponent;
import com.prototype.splitwise.entity.EntityRepository;
import org.springframework.stereotype.Repository;

@MongoComponent
@Repository
interface UserRepository extends EntityRepository<User> {
}
