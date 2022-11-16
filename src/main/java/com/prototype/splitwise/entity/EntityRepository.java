package com.prototype.splitwise.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;

public interface EntityRepository<E extends Entity> extends MongoRepository<E, String> {

    @Query(value = "{ 'meta.audit.createdOn' : { $gte : ?0, $lt : ?1 } }")
    Page<E> findByCreatedBetween(Instant fromTime, Instant toTime, Pageable pageable);

}
