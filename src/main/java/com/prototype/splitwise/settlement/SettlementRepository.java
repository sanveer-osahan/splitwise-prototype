package com.prototype.splitwise.settlement;

import com.prototype.splitwise.config.MongoComponent;
import com.prototype.splitwise.entity.EntityRepository;
import com.prototype.splitwise.expense.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@MongoComponent
@Repository
interface SettlementRepository extends EntityRepository<Settlement> {

    List<Settlement> findAllByDataExpenseId(String expenseId);

    @Query(" { 'data.settler._id' : ?0 , 'meta.audit.createdOn' : { $gte : ?1, $lt : ?2 } }")
    Page<Settlement> findByUserIdAndCreatedBetween(String user, Instant fromTime, Instant toTime, Pageable pageable);

    @Query(" { 'data.settler._id' : ?0 , 'data.owner._id' : ?1, 'data.group._id' : ?2, 'data.pendingAmount' : { $ne : 0 } }")
    List<Settlement> findPendingSettlements(String settler, String owner, String group);

}
