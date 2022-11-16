package com.prototype.splitwise.settlement;

import com.prototype.splitwise.event.Event;
import com.prototype.splitwise.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * This service will listen to the pay settlements and use payment service to update the existing settlements
 */
@KafkaListener(
        topics = "expense_payments",
        groupId = "expense_payments"
)
@Component
final class ExpensePaymentListener extends EventListener<Event<Settlement>> {

    private final PaymentService paymentService;

    public ExpensePaymentListener(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public void consume(Event<Settlement> event) {
        paymentService.makePayment(event.getSource());
    }
}
