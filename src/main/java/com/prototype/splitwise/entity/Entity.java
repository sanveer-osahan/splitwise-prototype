package com.prototype.splitwise.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.MongoId;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Getter
@Setter
public abstract class Entity<T> {

    @MongoId private String id;
    @Valid @NotNull private Meta meta;
    @Valid @NotNull private T data;

    @Getter
    @Setter
    public static class Meta {
        @NotBlank private String name;
        private Audit audit;
    }

    @Getter
    @Setter
    public static class Audit {

        private String createdBy;
        private String lastModifiedBy;
        private Instant createdOn;
        private Instant lastModifiedOn;
    }

    public void createAudit(String user) {
        Audit audit = new Audit();
        Instant createdOn = Instant.now();
        audit.setCreatedBy(user);
        audit.setCreatedOn(createdOn);
        audit.setLastModifiedBy(user);
        audit.setLastModifiedOn(createdOn);
        this.getMeta().setAudit(audit);
    }

    public void modifyAudit(String user) {
        Audit audit = this.getMeta().getAudit();
        audit.setLastModifiedOn(Instant.now());
        audit.setLastModifiedBy(user);
    }
}
