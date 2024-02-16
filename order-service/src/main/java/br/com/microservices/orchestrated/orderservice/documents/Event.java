package br.com.microservices.orchestrated.orderservice.documents;

import br.com.microservices.orchestrated.orderservice.SagaStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @EqualsAndHashCode.Include
    private String id;
    private String transactionalId;
    private String orderId;
    private Order payload;
    private String source;
    private SagaStatus status;
    private List<History> eventHistory;
    private LocalDateTime createdAt;
}
