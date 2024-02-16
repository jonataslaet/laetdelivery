package br.com.microservices.orchestrated.orchestratorservice.dtos;

import br.com.microservices.orchestrated.orchestratorservice.enums.EventSourceEnum;
import br.com.microservices.orchestrated.orchestratorservice.enums.SagaStatusEnum;
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
    private EventSourceEnum source;
    private SagaStatusEnum status;
    private List<History> eventHistory;
    private LocalDateTime createdAt;
}
