package br.com.microservices.orchestrated.orderservice.documents;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    private String code;
    private Double unitValue;

}