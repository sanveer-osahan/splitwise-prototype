package com.prototype.splitwise.user;

import com.prototype.splitwise.entity.Entity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@CompoundIndexes({
        @CompoundIndex(name = "users_created_on_index", def = "{ 'meta.audit.createdOn' : -1 }")
})
@Document(User.SERVICE)
@Getter
@Setter
public class User extends Entity<User.UserData> {

    public static final String ENTITY_TYPE = "user";
    public static final String SERVICE = ENTITY_TYPE + "s";

    @Getter
    @Setter
    public static class UserData {

        private String email;
        private String phone;
    }
}
