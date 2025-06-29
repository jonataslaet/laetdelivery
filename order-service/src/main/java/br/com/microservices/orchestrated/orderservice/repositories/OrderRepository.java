package br.com.microservices.orchestrated.orderservice.repositories;

import br.com.microservices.orchestrated.orderservice.documents.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository <Order, String> {
}
