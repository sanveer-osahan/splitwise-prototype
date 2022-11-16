package com.prototype.splitwise.event;

import org.springframework.kafka.annotation.KafkaHandler;

public abstract class EventListener<E extends Event> {

    @KafkaHandler(isDefault = true)
    public final void kafkaListen(E event) {
        event.loadContext();
        consume(event);
    }

    public abstract void consume(E event);
}
