package com.prototype.splitwise.entity;

import com.prototype.splitwise.event.Event;
import com.prototype.splitwise.event.EventHandler;

public abstract class EntityEventHandler<E extends Entity> extends EventHandler<Event<E>> {
    @Override
    public void consume(Event<E> event) {
        switch (event.getAction()) {
            case CREATE:
                onCreate(event);
                break;
            case UPDATE:
                onUpdate(event);
                break;
            case DELETE:
                onDelete(event);
                break;
        }
    }

    protected abstract void onCreate(Event<E> event);

    protected abstract void onUpdate(Event<E> event);

    protected abstract void onDelete(Event<E> event);
}
