package com.prototype.splitwise.settlement;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
final class PaymentService {

    private final SettlementService settlementService;
    private final MongoTemplate mongoTemplate;

    PaymentService(SettlementService settlementService, MongoTemplate mongoTemplate) {
        this.settlementService = settlementService;
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * MongoDB findAndModify operation is atomic. <br>
     * This will ensure that concurrent settlements will not corrupt the owner's settlement
     * @param settlement Settlement to make payment for
     */
    public void makePayment(Settlement settlement) {
        var settlementData = settlement.getData();
        var expenseId = settlementData.getExpense().getId();
        var owner = settlementData.getOwner().getId();
        double pendingAmount = settlementData.getPendingAmount();

        Query query =
                new Query().addCriteria(Criteria.where("data.expense._id").is(expenseId))
                .addCriteria(Criteria.where("data.settler._id").is(owner))
                .addCriteria(Criteria.where("data.owner._id").is(owner));

        var update = new Update().inc("data.pendingAmount", pendingAmount);
        mongoTemplate.findAndModify(query, update, Settlement.class, Settlement.SERVICE);
        settlement.getData().setPendingAmount(0);
        settlementService.updateEntity(settlement);
    }
}
