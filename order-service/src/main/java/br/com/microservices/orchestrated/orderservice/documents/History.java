package br.com.microservices.orchestrated.orderservice.documents;

import br.com.microservices.orchestrated.orderservice.enums.SagaStatusEnum;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class History {

    private String source;
    private SagaStatusEnum status;
    private String message;
    private LocalDateTime createdAt;
}
