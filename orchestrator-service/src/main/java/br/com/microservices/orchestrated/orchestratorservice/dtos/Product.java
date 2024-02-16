package br.com.microservices.orchestrated.orchestratorservice.dtos;

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
