package br.com.microservices.orchestrated.orderservice.documents;

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
