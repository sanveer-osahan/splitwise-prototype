package com.prototype.splitwise.entity;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Getter
@Setter
public class IDNameReference {

    @NotBlank private String id;
    private String name;

    private IDNameReference() {}

    private IDNameReference(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public static IDNameReference of(Entity entity) {
        return new IDNameReference(entity.getId(), entity.getMeta().getName());
    }

    public static IDNameReference of(String id, String name) {
        return new IDNameReference(id, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IDNameReference that = (IDNameReference) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
