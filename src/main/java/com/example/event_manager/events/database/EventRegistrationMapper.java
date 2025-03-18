package com.example.event_manager.events.database;

import com.example.event_manager.events.domain.EventRegistration;
import com.example.event_manager.events.domain.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class EventRegistrationMapper {

    @Autowired
    @Lazy
    private EventService eventService;

    @Autowired
    private EventEntityMapper eventEntityMapper;

    public EventRegistration toDomain(
            EventRegistrationEntity eventRegistrationEntity
    ) {
        return new EventRegistration(
                eventRegistrationEntity.getId(),
                eventRegistrationEntity.getUserId(),
                eventRegistrationEntity.getEvent().getId()
        );
    }

    public EventRegistrationEntity toEntity(EventRegistration eventRegistration) {
        var event = eventService.getEventById(eventRegistration.eventId());
        return new EventRegistrationEntity(
                eventRegistration.id(),
                eventRegistration.userId(),
                eventEntityMapper.toEntity(event)
        );
    }
}
