package br.com.microservices.orchestrated.orderservice.documents;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Builder.Default
    private List<OrderProducts> products = new ArrayList<>();

    private LocalDateTime createdAt;
    private String transactionalId;
    private Double totalAmount;
    private Integer itemsTotalQuantity;
}
