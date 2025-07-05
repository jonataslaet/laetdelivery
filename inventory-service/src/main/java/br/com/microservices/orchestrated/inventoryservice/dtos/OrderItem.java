package br.com.microservices.orchestrated.inventoryservice.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    private Product product;
    private Integer quantity;

}
