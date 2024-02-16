package br.com.microservices.orchestrated.productvalidationservice.dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @EqualsAndHashCode.Include
    private String id;
    private List<OrderProducts> products;
    private LocalDateTime createdAt;
    private String transactionalId;
    private Double totalAmount;
    private Integer itemsTotalQuantity;
}
