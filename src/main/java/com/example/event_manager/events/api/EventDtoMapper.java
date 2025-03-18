package com.example.event_manager.events.api;

import com.example.event_manager.events.domain.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EventDtoMapper {


    private static Logger logger = LoggerFactory.getLogger(EventDtoMapper.class);


    public EventDto toDto(Event event) {
        logger.info("Converting event to EventDto");
        return new EventDto(
                event.id(),
                event.name(),
                event.ownerId(),
                event.maxPlaces(),
                event.occupiedPlaces(),
                event.date(),
                event.cost(),
                event.duration(),
                event.locationId(),
                event.status()
        );
    }
}
