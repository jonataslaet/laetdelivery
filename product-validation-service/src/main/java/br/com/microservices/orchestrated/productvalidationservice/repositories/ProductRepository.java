package br.com.microservices.orchestrated.productvalidationservice.repositories;

import br.com.microservices.orchestrated.productvalidationservice.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    Boolean existsByCode(String code);

}
