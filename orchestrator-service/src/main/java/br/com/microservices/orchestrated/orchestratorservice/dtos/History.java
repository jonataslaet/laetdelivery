package br.com.microservices.orchestrated.orchestratorservice.dtos;

import br.com.microservices.orchestrated.orchestratorservice.EventSourceEnum;
import br.com.microservices.orchestrated.orchestratorservice.SagaStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class History {

    private EventSourceEnum source;
    private SagaStatus status;
    private String message;
    private LocalDateTime createdAt;
}
