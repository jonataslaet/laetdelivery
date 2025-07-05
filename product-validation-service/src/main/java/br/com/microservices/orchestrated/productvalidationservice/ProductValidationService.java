package br.com.microservices.orchestrated.productvalidationservice;

import br.com.microservices.orchestrated.productvalidationservice.dtos.Event;
import br.com.microservices.orchestrated.productvalidationservice.dtos.History;
import br.com.microservices.orchestrated.productvalidationservice.dtos.OrderItem;
import br.com.microservices.orchestrated.productvalidationservice.entities.Validation;
import br.com.microservices.orchestrated.productvalidationservice.enums.SagaStatusEnum;
import br.com.microservices.orchestrated.productvalidationservice.exceptions.ValidationException;
import br.com.microservices.orchestrated.productvalidationservice.producers.KafkaProducer;
import br.com.microservices.orchestrated.productvalidationservice.repositories.ProductRepository;
import br.com.microservices.orchestrated.productvalidationservice.repositories.ValidationRepository;
import br.com.microservices.orchestrated.productvalidationservice.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Service
@AllArgsConstructor
public class ProductValidationService {

    private static final String CURRENT_SOURCE = "PRODUCT_VALIDATION_SERVICE";

    private final JsonUtil jsonUtil;
    private final KafkaProducer kafkaProducer;
    private final ProductRepository productRepository;
    private final ValidationRepository validationRepository;

    public void validateExistingProducts(Event event) {
        try {
            checkCurrentValidation(event);
            createValidation(event, true);
            handleSuccess(event);
        } catch (Exception ex) {
            log.error("Error trying to validate products: ", ex);
            handleFailCurrentNotExecuted(event, ex.getMessage());
        }
        kafkaProducer.sendEvent(jsonUtil.toJson(event));
    }

    private void handleFailCurrentNotExecuted(Event event, String message) {
        event.setSource(CURRENT_SOURCE);
        event.setStatus(SagaStatusEnum.ROLLBACK_PENDING);
        addHistory(event, "Fail to validate products: ".concat(message));
    }

    public void rollbackEvent(Event event) {
        changeValidationToFail(event);
        event.setSource(CURRENT_SOURCE);
        event.setStatus(SagaStatusEnum.FAIL);
        addHistory(event, "Rollback executed on product validation");
        kafkaProducer.sendEvent(jsonUtil.toJson(event));
    }

    private void changeValidationToFail(Event event) {
        validationRepository
            .findByOrderIdAndTransactionId(event.getPayload().getId(), event.getTransactionId())
            .ifPresentOrElse(validation -> {
                validation.setSuccess(false);
                validationRepository.save(validation);
            }, () -> createValidation(event, false));
    }

    private void validateProductsInformed(Event event) {
        if (isEmpty(event.getPayload()) || isEmpty(event.getPayload().getOrderItems())) {
            throw new ValidationException("Product list is empty");
        }
        if (isEmpty(event.getPayload().getId()) || isEmpty(event.getPayload().getTransactionId())) {
            throw new ValidationException("OrderID and TransactionID must be informed");
        }
    }

    private void checkCurrentValidation(Event event) {
        validateProductsInformed(event);
        if (validationRepository.existsByOrderIdAndTransactionId(
            event.getOrderId(), event.getTransactionId())) {
            throw new ValidationException("There's another transactionId for this validation");
        }
        event.getPayload().getOrderItems().forEach(product -> {
            validateProductInformed(product);
            validateExistingProduct(product.getProduct().getCode());
        });
    }

    private void validateProductInformed(OrderItem product) {
        if (isEmpty(product.getProduct()) || isEmpty(product.getProduct().getCode())) {
            throw new ValidationException("Product must be informed");
        }
    }

    private void validateExistingProduct(String code) {
        if (!productRepository.existsByCode(code)) {
            throw new ValidationException("Product does not exist in database");
        }
    }

    private void createValidation(Event event, boolean success) {
        var validation = Validation.builder()
            .orderId(event.getPayload().getId())
            .transactionId(event.getTransactionId())
            .success(success)
            .build();
        validationRepository.save(validation);
    }

    private void handleSuccess(Event event) {
        event.setSource(CURRENT_SOURCE);
        event.setStatus(SagaStatusEnum.SUCCESS);
        addHistory(event, "Products are validated successfully");
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
}
