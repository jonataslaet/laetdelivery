package br.com.microservices.orchestrated.orchestratorservice.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProducts {

    private Product product;
    private Integer quantity;

}
