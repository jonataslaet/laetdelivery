package br.com.microservices.orchestrated.orderservice.services;

import br.com.microservices.orchestrated.orderservice.documents.Event;
import br.com.microservices.orchestrated.orderservice.documents.Order;
import br.com.microservices.orchestrated.orderservice.dtos.OrderRequest;
import br.com.microservices.orchestrated.orderservice.producers.SagaProducer;
import br.com.microservices.orchestrated.orderservice.repositories.OrderRepository;
import br.com.microservices.orchestrated.orderservice.utils.JsonUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderService {

    private static final String TRANSACTION_ID_PATTERN = "%s_%s";

    private final SagaProducer sagaProducer;
    private final EventService eventService;
    private final OrderRepository orderRepository;
    private final JsonUtil jsonUtil;

    public Order createOrder(OrderRequest orderRequest) {
        Order order = getOrderFromBuilder(orderRequest);
        orderRepository.save(order);
        Event event = createEvent(order);
        String eventPayload = jsonUtil.toJson(event);
        sagaProducer.sendEvent(eventPayload);
        return order;
    }

    private Order getOrderFromBuilder(OrderRequest orderRequest) {
        return Order.builder()
            .orderItems(orderRequest.getOrderItems())
            .createdAt(LocalDateTime.now())
            .transactionId(
                String.format(TRANSACTION_ID_PATTERN, Instant.now().toEpochMilli(), UUID.randomUUID())
            )
            .build();
    }

    private Event createEvent(Order order) {
        Event event = getEventFromBuilder(order);
        return eventService.save(event);
    }

    private Event getEventFromBuilder(Order order) {
        return Event.builder()
            .orderId(order.getId())
            .payload(order)
            .transactionId(order.getTransactionId())
            .createdAt(LocalDateTime.now())
            .build();
    }
}
