package br.com.microservices.orchestrated.paymentservice.services;

import br.com.microservices.orchestrated.paymentservice.dtos.Event;
import br.com.microservices.orchestrated.paymentservice.dtos.OrderItem;
import br.com.microservices.orchestrated.paymentservice.entities.Payment;
import br.com.microservices.orchestrated.paymentservice.exceptions.ValidationException;
import br.com.microservices.orchestrated.paymentservice.producers.KafkaProducer;
import br.com.microservices.orchestrated.paymentservice.repositories.PaymentRepository;
import br.com.microservices.orchestrated.paymentservice.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class PaymentService {

    private static final String CURRENT_SOURCE = "PAYMENT_SERVICE";
    public static final Double REDUCE_SUM_VALUE = 0.0;

    private final JsonUtil jsonUtil;
    private final KafkaProducer kafkaProducer;
    private final PaymentRepository paymentRepository;

    public void realizePayment(Event event) {
        try {
            checkCurrentValidation(event);
            createPendingPayment(event);
        } catch (Exception ex) {
            log.error("Error trying to make payment: ", ex);
        }
        kafkaProducer.sendEvent(jsonUtil.toJson(event));
    }

    private void checkCurrentValidation(Event event) {
        if (paymentRepository.existsByOrderIdAndTransactionId(
            event.getOrderId(), event.getTransactionId())) {
            throw new ValidationException("There's another transactionId for this validation");
        }
    }

    private void createPendingPayment(Event event) {
        var totalItems = calculateTotalItems(event);
        var totalAmount = calculateAmount(event);
        var payment = Payment.builder()
            .orderId(event.getPayload().getId())
            .transactionId(event.getTransactionId())
            .totalAmount(totalAmount)
            .totalItems(totalItems)
            .build();
        savePayment(payment);
        setEventAmountItems(event, payment);
    }

    private void setEventAmountItems(Event event, Payment payment) {
        event.getPayload().setTotalAmount(payment.getTotalAmount());
        event.getPayload().setItemsTotalQuantity(payment.getTotalItems());
    }

    private void savePayment(Payment payment) {
        paymentRepository.save(payment);
    }

    private double calculateAmount(Event event) {
        return event.getPayload().getOrderItems().stream().map(orderItem ->
            orderItem.getProduct().getUnitValue() * orderItem.getQuantity()).reduce(REDUCE_SUM_VALUE, Double::sum);
    }

    private int calculateTotalItems(Event event) {
        return event.getPayload().getOrderItems().stream().map(OrderItem::getQuantity)
            .reduce(REDUCE_SUM_VALUE.intValue(), Integer::sum);
    }
}

