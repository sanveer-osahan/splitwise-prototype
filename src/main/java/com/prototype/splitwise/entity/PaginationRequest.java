package com.prototype.splitwise.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.Objects;

@Getter
@Builder
public class PaginationRequest {

    private final Instant fromTime;
    private final Instant toTime;

    private final int pageSize;

    private final int pageNo;

    private final String sortOrder;

    private PaginationRequest(Instant fromTime, Instant toTime, int pageSize, int pageNo, String sortOrder) {
        this.fromTime = Objects.nonNull(fromTime) ? fromTime : Instant.EPOCH;
        this.toTime = Objects.nonNull(toTime) ? toTime : Instant.now();
        this.pageSize = pageSize;
        this.pageNo = pageNo;
        this.sortOrder = sortOrder;
    }

    public Pageable getPageable() {
        try {
            var direction = Sort.Direction.valueOf(sortOrder);
            return PageRequest.of(pageNo, pageSize, Sort.by(direction, "meta.audit.createdOn"));
        } catch (Exception e) {
            return PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "meta.audit.createdOn"));
        }
    }
}
