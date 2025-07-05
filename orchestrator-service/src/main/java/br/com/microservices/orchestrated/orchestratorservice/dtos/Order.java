package br.com.microservices.orchestrated.orchestratorservice.dtos;

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
    private List<OrderItem> orderItems;
    private LocalDateTime createdAt;
    private String transactionId;
    private Double totalAmount;
    private Integer itemsTotalQuantity;
}
