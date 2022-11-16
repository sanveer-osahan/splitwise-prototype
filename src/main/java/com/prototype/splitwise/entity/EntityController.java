package com.prototype.splitwise.entity;

import com.prototype.splitwise.config.AuthContext;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;

public abstract class EntityController<E extends Entity> {

    private final EntityService<E> entityService;

    protected EntityController(EntityService<E> entityService) {
        this.entityService = entityService;
    }

    @PostMapping
    public ResponseEntity<E> save(
            @RequestHeader(value = AuthContext.USER_ID, required = false) String currentUser,
            @Valid @RequestBody E resource) {
        var createdEntity = entityService.createEntity(resource);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEntity);
    }

    @PutMapping
    public ResponseEntity<E> update(
            @RequestHeader(value = AuthContext.USER_ID, required = false) String currentUser,
            @Valid @RequestBody E resource) {
         var updatedEntity = entityService.updateEntity(resource);
        return ResponseEntity.ok(updatedEntity);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Boolean> delete(
            @RequestHeader(value = AuthContext.USER_ID, required = false) String currentUser,
            @PathVariable String id) {
        entityService.deleteEntity(id);
        return ResponseEntity.ok(Boolean.TRUE);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<E> fetch(
            @RequestHeader(value = AuthContext.USER_ID, required = false) String currentUser,
            @PathVariable String id) {
        var fetchedEntity = entityService.getEntity(id);
        return ResponseEntity.ok(fetchedEntity);
    }

    @GetMapping
    public ResponseEntity<Page<E>> fetchPaginated(
            @RequestHeader(value = AuthContext.USER_ID, required = false) String currentUser,
            @RequestParam(value = "fromTime", required = false) Instant fromTime,
            @RequestParam(value = "toTime", required = false) Instant toTime,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam(value = "sortOrder", required = false) String sortOrder) {
        var paginationRequest = PaginationRequest.builder()
                .fromTime(fromTime)
                .toTime(toTime)
                .sortOrder(sortOrder)
                .pageNo(pageNo)
                .pageSize(pageSize)
                .build();
        var paginatedResponse = entityService.getPaginatedResponse(paginationRequest);
        return ResponseEntity.ok(paginatedResponse);
    }

}
