package com.prototype.splitwise.entity;

import com.prototype.splitwise.config.AuthContext;
import com.prototype.splitwise.event.Action;
import com.prototype.splitwise.event.Event;
import com.prototype.splitwise.exception.ClientException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Objects;

public abstract class EntityService<E extends Entity> {

    private final EntityRepository<E> entityRepository;

    private final KafkaTemplate<String, Event<E>> kafkaTemplate;

    protected EntityService(
            EntityRepository<E> entityRepository, KafkaTemplate<String, Event<E>> kafkaTemplate) {
        this.entityRepository = entityRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public E createEntity(E resource) {
        if (Objects.nonNull(resource.getId()) && entityRepository.existsById(resource.getId())) {
            throw new ClientException(HttpStatus.CONFLICT,
                    String.format("No %s found with id : %s", getEntityType(), resource.getId()));
        }
        beforeCreate(resource);
        var createdEntity = entityRepository.save(resource);
        publish(createdEntity, Action.CREATE);
        return createdEntity;
    }

    public E updateEntity(E resource) {
        var oldResource = getEntity(resource.getId());
        beforeUpdate(oldResource, resource);
        var updatedEntity = entityRepository.save(resource);
        publish(updatedEntity, Action.UPDATE);
        return updatedEntity;
    }

    public E getEntity(String id) {
        return entityRepository.findById(id)
                        .orElseThrow(() -> new ClientException(HttpStatus.NOT_FOUND,
                                String.format("No %s found with id : %s", getEntityType(), id)));
    }

    public void deleteEntity(String id) {
        var resource = getEntity(id);
        beforeDelete(resource);
        deleteEntity(resource);
    }

    public void deleteEntity(E resource) {
        entityRepository.delete(resource);
        publish(resource, Action.DELETE);
    }

    public Page<E> getPaginatedResponse(PaginationRequest paginationRequest) {
        return entityRepository.findByCreatedBetween(
                paginationRequest.getFromTime(),
                paginationRequest.getToTime(),
                paginationRequest.getPageable());
    }

    private void publish(E source, Action action) {
        kafkaTemplate.send("crud_" + getEntityType(), Event.of(source, action));
    }

    protected void beforeCreate(E resource) {
        resource.createAudit(AuthContext.getCurrentUserOrElse("admin"));
    }

    protected void beforeUpdate(E oldResource, E newResource) {
        var oldAudit = oldResource.getMeta().getAudit();
        newResource.getMeta().setAudit(oldAudit);
        newResource.modifyAudit(AuthContext.getCurrentUserOrElse("admin"));
    }

    protected void beforeDelete(E resource) {
    }

    protected abstract String getEntityType();
}
