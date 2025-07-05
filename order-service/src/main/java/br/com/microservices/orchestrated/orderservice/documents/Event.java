package br.com.microservices.orchestrated.orderservice.documents;

import br.com.microservices.orchestrated.orderservice.enums.SagaStatusEnum;
import lombok.*;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "order")
public class Event {

    @Id
    @EqualsAndHashCode.Include
    private String id;
    private String transactionId;
    private String orderId;
    private Order payload;
    private String source;
    private SagaStatusEnum status;
    private List<History> eventHistory;
    private LocalDateTime createdAt;
}
