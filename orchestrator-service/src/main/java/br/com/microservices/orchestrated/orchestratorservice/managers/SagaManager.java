package br.com.microservices.orchestrated.orchestratorservice.managers;

import br.com.microservices.orchestrated.orchestratorservice.dtos.Event;
import br.com.microservices.orchestrated.orchestratorservice.enums.TopicsEnum;
import br.com.microservices.orchestrated.orchestratorservice.exceptions.ValidationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;

import static br.com.microservices.orchestrated.orchestratorservice.handlers.SagaHandler.*;

@Slf4j
@Component
@AllArgsConstructor
public class SagaManager {

    public static final String LOG = "ORDER ID: %s | TRANSACTION ID: %s | EVENT ID: %s";

    public TopicsEnum getNextTopic(Event event) {
        if ((ObjectUtils.isEmpty(event.getSource())) || (ObjectUtils.isEmpty(event.getStatus()))) {
            throw new ValidationException("Source and Status must be informed");
        }
        var topicEnum = findTopicBySourceAndStatus(event);
        logCurrentSaga(event, topicEnum);
        return topicEnum;
    }

    private TopicsEnum findTopicBySourceAndStatus(Event event) {
        return (TopicsEnum) (Arrays.stream(SAGA_HANDLER).filter(row -> isValidEventSourceAndStatus(event, row))
            .map(row -> row[TOPIC_INDEX]).findFirst().orElseThrow(() -> new ValidationException("Topic not found")));
    }

    private boolean isValidEventSourceAndStatus(Event event, Object[] row) {
        var source = row[EVENT_SOURCE_INDEX];
        var status = row[SAGA_STATUS_INDEX];
        return event.getSource().equals(source) && event.getStatus().equals(status);
    }

    private void logCurrentSaga(Event event, TopicsEnum topicsEnum) {
        var sagaId = createSagaId(event);
        var source = event.getSource();
        switch (event.getStatus()) {
            case SUCCESS -> log.info("CURRENT SAGA: {} | SUCCESS | NEXT TOPIC: {} | {}",
                source, topicsEnum, sagaId);
            case ROLLBACK_PENDING -> log.info("CURRENT SAGA: {} | SENDING TO ROLLBACK CURRENT SERVICE | NEXT TOPIC: {} | {}",
                source, topicsEnum, sagaId);
            case FAIL -> log.info("CURRENT SAGA: {} | SENDING TO ROLLBACK PREVIOUS SERVICE | NEXT TOPIC: {} | {}",
                source, topicsEnum, sagaId);
        }
    }

    private String createSagaId(Event event) {
        return String.format(LOG, event.getPayload().getId(), event.getPayload().getTransactionId(), event.getId());
    }
}
