package com.prototype.splitwise.user;

import com.prototype.splitwise.entity.Entity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(User.SERVICE)
@Getter
@Setter
public class User extends Entity<User.Data> {

    public static final String ENTITY_TYPE = "user";
    public static final String SERVICE = ENTITY_TYPE + "s";

    @Getter
    @Setter
    public static class Data {

        private String email;
        private String phone;
    }
}
