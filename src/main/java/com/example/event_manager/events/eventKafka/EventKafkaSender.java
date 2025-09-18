package com.example.event_manager.events.eventKafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventKafkaSender {

    private static final Logger log = LoggerFactory.getLogger(EventKafkaSender.class);

    private final KafkaTemplate<Long, EventChangeMessage> kafkaTemplate;

    public EventKafkaSender(KafkaTemplate<Long, EventChangeMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(EventChangeMessage event) {
        log.info("Sending event: {}", event);

        var result = kafkaTemplate.send(
                "event-topic",
                event.eventId(),
                event
        );

        result.thenAccept(longEventKafkaEventSendResult -> {
            log.info("Event send successful: {}", event);
        });
    }
}
