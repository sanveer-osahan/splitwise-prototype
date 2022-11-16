package com.prototype.splitwise.group;

import com.prototype.splitwise.config.MongoComponent;
import com.prototype.splitwise.entity.EntityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@MongoComponent
@Repository
interface GroupRepository extends EntityRepository<Group> {

    @Query(value = "{ 'data.users.id' : ?0 }")
    Page<Group> findByUserId(String user, Pageable pageable);
}
