package br.com.microservices.orchestrated.orderservice.repositories;

import br.com.microservices.orchestrated.orderservice.documents.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends MongoRepository <Event, String> {
    List<Event> findAllByOrderByCreatedAtDesc();

    Optional<Event> findTop1ByOrderIdOrderByCreatedAtDesc(String orderId);
    Optional<Event> findTop1ByTransactionIdOrderByCreatedAtDesc(String transactionId);
}
