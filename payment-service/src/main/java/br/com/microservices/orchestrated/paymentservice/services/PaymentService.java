package br.com.microservices.orchestrated.paymentservice.services;

import br.com.microservices.orchestrated.paymentservice.dtos.Event;
import br.com.microservices.orchestrated.paymentservice.dtos.History;
import br.com.microservices.orchestrated.paymentservice.dtos.OrderItem;
import br.com.microservices.orchestrated.paymentservice.entities.Payment;
import br.com.microservices.orchestrated.paymentservice.enums.PaymentStatusEnum;
import br.com.microservices.orchestrated.paymentservice.enums.SagaStatusEnum;
import br.com.microservices.orchestrated.paymentservice.exceptions.ValidationException;
import br.com.microservices.orchestrated.paymentservice.producers.KafkaProducer;
import br.com.microservices.orchestrated.paymentservice.repositories.PaymentRepository;
import br.com.microservices.orchestrated.paymentservice.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class PaymentService {

    private static final String CURRENT_SOURCE = "PAYMENT_SERVICE";
    public static final Double REDUCE_SUM_VALUE = 0.0;
    private static final Double MINIMUN_AMOUNT_VALUE = 0.01;

    private final JsonUtil jsonUtil;
    private final KafkaProducer kafkaProducer;
    private final PaymentRepository paymentRepository;

    public void realizePayment(Event event) {
        try {
            checkCurrentValidation(event);
            createPendingPayment(event);
            var payment = findByOrderIdAndTransactionId(event);
            validateAmount(payment.getTotalAmount());
            changePaymentToSuccess(payment);
            handleSuccess(event);
        } catch (Exception ex) {
            log.error("Error trying to make payment: ", ex);
            handleFailCurrentNotExecuted(event, ex.getMessage());
        }
        kafkaProducer.sendEvent(jsonUtil.toJson(event));
    }

    private void handleFailCurrentNotExecuted(Event event, String message) {
        event.setSource(CURRENT_SOURCE);
        event.setStatus(SagaStatusEnum.ROLLBACK_PENDING);
        addHistory(event, "Fail to realize payment: ".concat(message));
    }

    public void realizeRefund(Event event) {
        event.setStatus(SagaStatusEnum.FAIL);
        event.setSource(CURRENT_SOURCE);
        try {
            changePaymentStatusToRefund(event);
            addHistory(event, "Rollback executed for payment");
        } catch (Exception e) {
            addHistory(event, "Rollback not executed for payment: ".concat(e.getMessage()));
        }
        kafkaProducer.sendEvent(jsonUtil.toJson(event));
    }

    private void changePaymentStatusToRefund(Event event) {
        var payment = findByOrderIdAndTransactionId(event);
        payment.setStatus(PaymentStatusEnum.REFUND);
        setEventAmountItems(event, payment);
        savePayment(payment);
    }

    private void handleSuccess(Event event) {
        event.setSource(CURRENT_SOURCE);
        event.setStatus(SagaStatusEnum.SUCCESS);
        addHistory(event, "Payment realized successfully");
    }

    private void addHistory(Event event, String message) {
        var history = History.builder()
            .source(event.getSource())
            .status(event.getStatus())
            .message(message)
            .createdAt(LocalDateTime.now())
            .build();
        event.addHistory(history);
    }

    private void changePaymentToSuccess(Payment payment) {
        payment.setStatus(PaymentStatusEnum.SUCCESS);
        savePayment(payment);
    }

    private void validateAmount(double amount) {
        if (amount < 0.01) {
            throw new ValidationException("Amount must be equal or greater than ".concat(MINIMUN_AMOUNT_VALUE.toString()));
        }
    }

    private Payment findByOrderIdAndTransactionId(Event event) {
        return paymentRepository.findByOrderIdAndTransactionId(event.getPayload().getId(),
            event.getPayload().getTransactionId()).orElseThrow(() ->
            new ValidationException("Payment not found by OrderOD and TransactionID"));
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

