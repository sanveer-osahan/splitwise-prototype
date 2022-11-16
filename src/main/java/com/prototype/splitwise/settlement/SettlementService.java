package com.prototype.splitwise.settlement;

import com.prototype.splitwise.config.AuthContext;
import com.prototype.splitwise.entity.EntityService;
import com.prototype.splitwise.entity.IDNameReference;
import com.prototype.splitwise.entity.PaginationRequest;
import com.prototype.splitwise.event.Action;
import com.prototype.splitwise.event.Event;
import com.prototype.splitwise.exception.ClientException;
import com.prototype.splitwise.group.Group;
import com.prototype.splitwise.user.User;
import org.springframework.data.domain.Page;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
final class SettlementService extends EntityService<Settlement> {

    private final SettlementRepository settlementRepository;
    private final EntityService<User> userService;
    private final EntityService<Group> groupService;
    private final KafkaTemplate<String, Event<Settlement>> kafkaTemplate;

    private SettlementService(SettlementRepository settlementRepository, KafkaTemplate<String, Event<Settlement>> kafkaTemplate, EntityService<User> userService, EntityService<Group> groupService, KafkaTemplate<String, Event<Settlement>> kafkaTemplate1) {
        super(settlementRepository, kafkaTemplate);
        this.settlementRepository = settlementRepository;
        this.userService = userService;
        this.groupService = groupService;
        this.kafkaTemplate = kafkaTemplate1;
    }

    public List<Settlement> getSettlements(String expenseId) {
        return settlementRepository.findAllByDataExpenseId(expenseId);
    }

    public Page<Settlement> getSettlements(PaginationRequest paginationRequest) {
        return settlementRepository.findByUserIdAndCreatedBetween(
                AuthContext.getCurrentUserOrElseThrow(),
                paginationRequest.getFromTime(),
                paginationRequest.getToTime(),
                paginationRequest.getPageable());
    }

    public SettlementResponse getDues(SettlementRequest request) {
        final String currentUser = AuthContext.getCurrentUserOrElseThrow();
        final String otherUser = request.getUser();
        final String group = request.getGroup();

        // Validate user and group
        if (currentUser.equals(otherUser)) {
            throw new ClientException("Requested user cannot be same as the current authenticated user");
        }
        userService.getEntity(otherUser);
        var groupUsers = groupService.getEntity(group).getData().getUsers().stream()
                .map(IDNameReference::getId)
                .collect(Collectors.toSet());

        validateUser(currentUser, group, groupUsers);
        validateUser(otherUser, group, groupUsers);

        var debts = settlementRepository.findPendingSettlements(currentUser, otherUser, group);
        var credits = settlementRepository.findPendingSettlements(otherUser, currentUser, group);
        return SettlementResponse.of(debts, credits);
    }

    public SettlementResponse makePayments(SettlementRequest request) {
        var response = getDues(request);
        response.getPay().forEach(this::sendPayment);
        response.getReceive().forEach(this::sendPayment);
        return response;
    }

    private void sendPayment(Settlement settlement) {
        kafkaTemplate.send("expense_payments", Event.of(settlement, Action.UPDATE));
    }

    private void validateUser(String user, String group, Set<String> groupUsers) {
        if (!groupUsers.contains(user)) {
            throw new ClientException(user + " does not exist in group " + group);
        }
    }

    @Override
    protected String getEntityType() {
        return Settlement.ENTITY_TYPE;
    }
}
