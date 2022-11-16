package com.prototype.splitwise.event;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.prototype.splitwise.config.AuthContext;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
public class Event<T> {

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    private T source;
    private Class<T> sourceType;
    private Action action;
    private String currentUser;
    private Instant createdOn;

    private Event() {}

    private Event(T source, Action action) {
        this.source = source;
        this.action = action;
        this.sourceType = (Class<T>) source.getClass();
        this.createdOn = Instant.now();
        this.currentUser = AuthContext.getCurrentUserOrElse("admin");
    }

    public static <T> Event<T> of(T source, Action action) {
        return new Event<>(source, action);
    }

    public void loadContext() {
        AuthContext.load(this.currentUser);
    }
}
