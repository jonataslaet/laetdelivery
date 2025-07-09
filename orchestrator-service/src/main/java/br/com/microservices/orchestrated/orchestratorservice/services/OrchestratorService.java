package br.com.microservices.orchestrated.orchestratorservice.services;

import br.com.microservices.orchestrated.orchestratorservice.dtos.Event;
import br.com.microservices.orchestrated.orchestratorservice.dtos.History;
import br.com.microservices.orchestrated.orchestratorservice.enums.EventSourceEnum;
import br.com.microservices.orchestrated.orchestratorservice.enums.SagaStatusEnum;
import br.com.microservices.orchestrated.orchestratorservice.enums.TopicsEnum;
import br.com.microservices.orchestrated.orchestratorservice.managers.SagaManager;
import br.com.microservices.orchestrated.orchestratorservice.producers.SagaOrchestratorProducer;
import br.com.microservices.orchestrated.orchestratorservice.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static br.com.microservices.orchestrated.orchestratorservice.enums.TopicsEnum.NOTIFY_ENDING;

@Slf4j
@Service
@AllArgsConstructor
public class OrchestratorService {

    private final JsonUtil jsonUtil;
    private final SagaOrchestratorProducer sagaOrchestratorProducer;
    private final SagaManager sagaManager;

    public void startSaga(Event event) {
        event.setSource(EventSourceEnum.ORCHESTRATOR);
        event.setStatus(SagaStatusEnum.SUCCESS);
        TopicsEnum topicEnum = getTopic(event);
        log.info("SAGA STARTED");
        addHistory(event, "Saga started");
        sagaOrchestratorProducer.sendEvent(jsonUtil.toJson(event), topicEnum.getTopic());
    }

    private TopicsEnum getTopic(Event event) {
        return sagaManager.getNextTopic(event);
    }

    public void finishSagaSuccess(Event event) {
        event.setSource(EventSourceEnum.ORCHESTRATOR);
        event.setStatus(SagaStatusEnum.SUCCESS);
        log.info("SAGA FINISHED SUCCESSFULLY FOR EVENT {}", event.getId());
        addHistory(event, "Saga finished succefully");
        sagaOrchestratorProducer.sendEvent(jsonUtil.toJson(event), NOTIFY_ENDING.getTopic());
    }

    public void finishSagaFail(Event event) {
        event.setSource(EventSourceEnum.ORCHESTRATOR);
        event.setStatus(SagaStatusEnum.FAIL);
        log.info("SAGA FINISHED WITH ERRORS FOR EVENT {}", event.getId());
        addHistory(event, "Saga finished with errors");
        sagaOrchestratorProducer.sendEvent(jsonUtil.toJson(event), NOTIFY_ENDING.getTopic());
    }

    public void continueSaga(Event event) {
        var topicEnum = getTopic(event);
        log.info("SAGA CONTINUING FOR EVENT {}", event.getId());
        sagaOrchestratorProducer.sendEvent(jsonUtil.toJson(event), topicEnum.getTopic());
    }

    private void addHistory(Event event, String message) {
        var history = History.builder()
            .source(event.getSource())
            .status(event.getStatus())
            .message(message)
            .createdAt(LocalDateTime.now())
            .build();
        event.addHistory(history);
    }
}
