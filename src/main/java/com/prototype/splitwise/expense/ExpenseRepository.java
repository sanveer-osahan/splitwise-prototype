package com.prototype.splitwise.expense;

import com.prototype.splitwise.config.MongoComponent;
import com.prototype.splitwise.entity.EntityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.Instant;

@MongoComponent
@Repository
interface ExpenseRepository extends EntityRepository<Expense> {

    @Query(" { '$or' : [ { 'data.owner._id' : ?0 }, { 'data.splits.user._id' : ?0 } ], 'meta.audit.createdOn' : { $gte : ?1, $lt : ?2 } }")
    Page<Expense> findByUserIdAndCreatedBetween(String user, Instant fromTime, Instant toTime, Pageable pageable);
}
