package br.com.microservices.orchestrated.orderservice.documents;

import lombok.*;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "order")
public class Order {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    private LocalDateTime createdAt;
    private String transactionId;
    private Double totalAmount;
    private Integer itemsTotalQuantity;
}
