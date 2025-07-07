package br.com.microservices.orchestrated.inventoryservice.services;

import br.com.microservices.orchestrated.inventoryservice.dtos.Event;
import br.com.microservices.orchestrated.inventoryservice.dtos.History;
import br.com.microservices.orchestrated.inventoryservice.dtos.Order;
import br.com.microservices.orchestrated.inventoryservice.dtos.OrderItem;
import br.com.microservices.orchestrated.inventoryservice.entities.Inventory;
import br.com.microservices.orchestrated.inventoryservice.entities.OrderInventory;
import br.com.microservices.orchestrated.inventoryservice.enums.SagaStatusEnum;
import br.com.microservices.orchestrated.inventoryservice.exceptions.ValidationException;
import br.com.microservices.orchestrated.inventoryservice.producers.KafkaProducer;
import br.com.microservices.orchestrated.inventoryservice.repositories.InventoryRepository;
import br.com.microservices.orchestrated.inventoryservice.repositories.OrderInventoryRepository;
import br.com.microservices.orchestrated.inventoryservice.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class InventoryService {

    private static final String CURRENT_SOURCE = "INVENTORY_SERVICE";

    private final JsonUtil jsonUtil;
    private final KafkaProducer kafkaProducer;
    private final InventoryRepository inventoryRepository;
    private final OrderInventoryRepository orderInventoryRepository;

    public void updateInventory(Event event) {
        try {
            checkCurrentValidation(event);
            createOrderInventory(event);
            updateInventoryByOrder(event.getPayload());
            handleSuccess(event);
        } catch (Exception ex) {
            log.error("Error trying to update inventory: ", ex);
            handleFailCurrentNotExecuted(event, ex.getMessage());
        }
        kafkaProducer.sendEvent(jsonUtil.toJson(event));
    }

    private void handleFailCurrentNotExecuted(Event event, String message) {
        event.setSource(CURRENT_SOURCE);
        event.setStatus(SagaStatusEnum.ROLLBACK_PENDING);
        addHistory(event, "Fail to update inventory: ".concat(message));
    }

    public void rollbackInventory(Event event) {
        event.setStatus(SagaStatusEnum.FAIL);
        event.setSource(CURRENT_SOURCE);
        try {
            returnInventoryToPreviousValues(event);
            addHistory(event, "Rollback executed for inventory");
        } catch (Exception e) {
            addHistory(event, "Rollback not executed for inventory: ".concat(e.getMessage()));
        }
        kafkaProducer.sendEvent(jsonUtil.toJson(event));
    }

    private void returnInventoryToPreviousValues(Event event) {
        orderInventoryRepository.findByOrderIdAndTransactionId(
            event.getPayload().getId(), event.getPayload().getTransactionId()).forEach(orderInventory -> {
                var inventory = orderInventory.getInventory();
                inventory.setAvailable(orderInventory.getOldQuantity());
                inventoryRepository.save(inventory);
                log.info("Restored inventory for order {} from {} to {}",
                    event.getPayload().getId(), orderInventory.getNewQuantity(), orderInventory.getOldQuantity());
        });
    }

    private void checkInventory(int available, int orderQuantity) {
        if (orderQuantity > available) {
            throw new ValidationException("Product is out of stock");
        }
    }

    private void handleSuccess(Event event) {
        event.setSource(CURRENT_SOURCE);
        event.setStatus(SagaStatusEnum.SUCCESS);
        addHistory(event, "Inventory updated successfully");
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

    private void createOrderInventory(Event event) {
        event.getPayload().getOrderItems().forEach(orderItem -> {
            var inventory = findInventoryByProductCode(orderItem.getProduct().getCode());
            var orderInventory = buildOrderInventory(event, orderItem, inventory);
            orderInventoryRepository.save(orderInventory);
        });
    }

    public void updateInventoryByOrder(Order order) {
        order.getOrderItems().forEach(orderItem -> {
            var inventory = findInventoryByProductCode(orderItem.getProduct().getCode());
            checkInventory(inventory.getAvailable(), orderItem.getQuantity());
            inventory.setAvailable(inventory.getAvailable() - orderItem.getQuantity());
            inventoryRepository.save(inventory);
        });
    }

    private OrderInventory buildOrderInventory(Event event, OrderItem orderItem, Inventory inventory) {
        return OrderInventory.builder()
            .inventory(inventory)
            .oldQuantity(inventory.getAvailable())
            .orderQuantity(orderItem.getQuantity())
            .newQuantity(inventory.getAvailable() - orderItem.getQuantity())
            .orderId(event.getPayload().getId())
            .transactionId(event.getPayload().getTransactionId())
            .build();
    }

    private Inventory findInventoryByProductCode(String productCode) {
        return inventoryRepository.findByProductCode(productCode).orElseThrow(() ->
            new ValidationException("Inventory not found by product code ".concat(productCode)));
    }

    private void checkCurrentValidation(Event event) {
        if (orderInventoryRepository.existsByOrderIdAndTransactionId(
            event.getOrderId(), event.getTransactionId())) {
            throw new ValidationException("There's another transactionId for this validation");
        }
    }
}
