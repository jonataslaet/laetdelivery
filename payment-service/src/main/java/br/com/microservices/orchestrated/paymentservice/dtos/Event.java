package br.com.microservices.orchestrated.paymentservice.dtos;

import br.com.microservices.orchestrated.paymentservice.enums.SagaStatusEnum;
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
    private SagaStatusEnum status;
    private List<History> eventHistory;
    private LocalDateTime createdAt;
}
