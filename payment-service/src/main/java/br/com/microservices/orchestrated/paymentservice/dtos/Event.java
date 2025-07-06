package br.com.microservices.orchestrated.paymentservice.dtos;

import br.com.microservices.orchestrated.paymentservice.enums.SagaStatusEnum;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.ObjectUtils.isEmpty;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @EqualsAndHashCode.Include
    private String id;
    private String transactionId;
    private String orderId;
    private Order payload;
    private String source;
    private SagaStatusEnum status;
    private List<History> eventHistory;
    private LocalDateTime createdAt;

    public void addHistory(History history) {
        if (isEmpty(eventHistory)) {
            this.eventHistory = new ArrayList<>();
        }
        this.eventHistory.add(history);
    }
}
