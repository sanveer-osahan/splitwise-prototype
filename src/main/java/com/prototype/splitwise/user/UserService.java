package com.prototype.splitwise.user;

import com.prototype.splitwise.entity.EntityRepository;
import com.prototype.splitwise.entity.EntityService;
import com.prototype.splitwise.event.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
final class UserService extends EntityService<User> {

    private UserService(
            EntityRepository<User> userRepository,
            KafkaTemplate<String, Event<User>> kafkaTemplate) {
        super(userRepository, kafkaTemplate);
    }

    @Override
    protected String getEntityType() {
        return User.ENTITY_TYPE;
    }
}
