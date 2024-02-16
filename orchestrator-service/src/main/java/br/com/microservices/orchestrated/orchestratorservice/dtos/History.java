package br.com.microservices.orchestrated.orchestratorservice.dtos;

import br.com.microservices.orchestrated.orchestratorservice.enums.EventSourceEnum;
import br.com.microservices.orchestrated.orchestratorservice.enums.SagaStatusEnum;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class History {

    private EventSourceEnum source;
    private SagaStatusEnum status;
    private String message;
    private LocalDateTime createdAt;
}
