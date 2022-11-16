package com.prototype.splitwise.group;

import com.prototype.splitwise.entity.Entity;
import com.prototype.splitwise.entity.IDNameReference;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Getter
@Setter
@Document(Group.SERVICE)
public class Group extends Entity<Group.Data> {

    public static final String ENTITY_TYPE = "group";
    public static final String SERVICE = ENTITY_TYPE + "s";

    @Getter
    @Setter
    public static class Data {

        @Valid @NotEmpty private Set<IDNameReference> users;
    }
}
