package br.com.microservices.orchestrated.orderservice.services;

import br.com.microservices.orchestrated.orderservice.documents.Event;
import br.com.microservices.orchestrated.orderservice.dtos.EventFilters;
import br.com.microservices.orchestrated.orderservice.exceptions.ValidationException;
import br.com.microservices.orchestrated.orderservice.repositories.EventRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public void notifyEnding(Event event) {
        event.setOrderId(event.getOrderId());
        event.setCreatedAt(LocalDateTime.now());
        save(event);
        log.info("Order {} with saga notified! TransactionId: {}", event.getOrderId(), event.getTransactionId());
    }

    public Event save(Event event) {
        return eventRepository.save(event);
    }

    public List<Event> findAll() {
        return eventRepository.findAllByOrderByCreatedAtDesc();
    }

    public Event findEvent(EventFilters filter) {
        validateEvent(filter);
        if (Objects.nonNull(filter.getOrderId())) {
            return eventRepository.findTop1ByOrderIdOrderByCreatedAtDesc(filter.getOrderId())
                .orElseThrow(() -> new ValidationException("Event not found by order ID " + filter.getOrderId()));
        }
        return eventRepository.findTop1ByTransactionIdOrderByCreatedAtDesc(filter.getTransactionId())
            .orElseThrow(() -> new ValidationException("Event not found by transactional ID " + filter.getTransactionId()));
    }

    private void validateEvent(EventFilters filter) {
        if (Objects.isNull(filter) || ((Objects.isNull(filter.getTransactionId()) || filter.getTransactionId().isEmpty())
            && (Objects.isNull(filter.getOrderId()) || filter.getOrderId().isEmpty()))) {
            throw new ValidationException("Order ID or Transaction ID must be informed");
        }
    }
}
