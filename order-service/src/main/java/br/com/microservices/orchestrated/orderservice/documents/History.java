package br.com.microservices.orchestrated.orderservice.documents;

import br.com.microservices.orchestrated.orderservice.SagaStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class History {

    private String source;
    private SagaStatus status;
    private String message;
    private LocalDateTime createdAt;
}
