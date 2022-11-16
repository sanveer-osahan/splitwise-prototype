package com.prototype.splitwise.settlement;

import com.prototype.splitwise.config.AuthContext;
import com.prototype.splitwise.entity.EntityService;
import com.prototype.splitwise.entity.PaginationRequest;
import com.prototype.splitwise.event.Event;
import org.springframework.data.domain.Page;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
final class SettlementService extends EntityService<Settlement> {

    private final SettlementRepository settlementRepository;

    private SettlementService(SettlementRepository settlementRepository, KafkaTemplate<String, Event<Settlement>> kafkaTemplate) {
        super(settlementRepository, kafkaTemplate);
        this.settlementRepository = settlementRepository;
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

    @Override
    protected String getEntityType() {
        return Settlement.ENTITY_TYPE;
    }
}
