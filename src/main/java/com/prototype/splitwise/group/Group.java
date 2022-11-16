package com.prototype.splitwise.group;

import com.prototype.splitwise.entity.Entity;
import com.prototype.splitwise.entity.IDNameReference;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@CompoundIndexes({
        @CompoundIndex(name = "group_users_index", def = "{ 'data.users._id': 1, 'meta.audit.createdOn' : -1 }")
})
@Getter
@Setter
@Document(Group.SERVICE)
public class Group extends Entity<Group.GroupData> {

    public static final String ENTITY_TYPE = "group";
    public static final String SERVICE = ENTITY_TYPE + "s";

    @Getter
    @Setter
    public static class GroupData {

        @Valid @NotEmpty private Set<IDNameReference> users;
    }
}
