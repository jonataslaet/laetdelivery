package br.com.microservices.orchestrated.paymentservice.dtos;

import br.com.microservices.orchestrated.paymentservice.enums.SagaStatusEnum;
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
