package com.example.event_manager.kafka;

import com.example.event_manager.events.eventKafka.EventChangeMessage;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.ssl.DefaultSslBundleRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.stereotype.Component;

@Component
public class KafkaConfiguration {

    @Bean
    public KafkaTemplate<Long, EventChangeMessage> kafkaTemplate(
            KafkaProperties kafkaProperties
    ) {

        var props = kafkaProperties.buildProducerProperties(
                new DefaultSslBundleRegistry()
        );

        ProducerFactory <Long, EventChangeMessage> producerFactory =
                new DefaultKafkaProducerFactory(props);

        return new KafkaTemplate<>(producerFactory);
    }
}
