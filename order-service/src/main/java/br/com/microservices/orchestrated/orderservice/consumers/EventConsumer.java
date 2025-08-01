package br.com.microservices.orchestrated.orderservice.consumers;

import br.com.microservices.orchestrated.orderservice.documents.Event;
import br.com.microservices.orchestrated.orderservice.services.EventService;
import br.com.microservices.orchestrated.orderservice.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class EventConsumer {

    private final JsonUtil jsonUtil;
    private final EventService eventService;

    @KafkaListener(
        groupId = "${spring.kafka.consumer.group-id}",
        topics = "${spring.kafka.topic.notify-ending}"
    )
    public void consumeNotifyEndingEvent(String payload) {
        log.info("Receiving ending notification event {} from notify-ending-topic", payload);
        Event event = jsonUtil.toEvent(payload);
        eventService.save(event);
    }
}
