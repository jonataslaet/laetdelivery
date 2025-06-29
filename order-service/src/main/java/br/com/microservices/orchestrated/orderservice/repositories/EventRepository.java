package br.com.microservices.orchestrated.orderservice.repositories;

import br.com.microservices.orchestrated.orderservice.documents.Event;
import br.com.microservices.orchestrated.orderservice.documents.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventRepository extends MongoRepository <Event, String> {
}
