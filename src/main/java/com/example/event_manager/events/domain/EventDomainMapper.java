package com.example.event_manager.events.domain;

import com.example.event_manager.events.api.EventDto;
import com.example.event_manager.events.database.EventEntity;
import com.example.event_manager.events.database.EventEntityMapper;
import com.example.event_manager.events.database.EventRegistrationMapper;
import org.springframework.stereotype.Component;

@Component
public class EventDomainMapper {

    private final EventRegistrationMapper eventRegistrationMapper;

    public EventDomainMapper(EventEntityMapper eventEntityMapper, EventRegistrationMapper eventRegistrationMapper) {
        this.eventRegistrationMapper = eventRegistrationMapper;
    }

    public Event toDomainFromEntity(EventEntity createdEvent) {
        return new Event(
                createdEvent.getId(),
                createdEvent.getName(),
                createdEvent.getOwnerId(),
                createdEvent.getPlaces(),
                createdEvent.getOccupiedPlaces(),
                createdEvent.getRegistrationList()
                        .stream()
                        .map(it -> new EventRegistration(
                                it.getId(),
                                it.getUserId(),
                                createdEvent.getId())
                        ).toList(),
                createdEvent.getDate(),
                createdEvent.getCost(),
                createdEvent.getDuration(),
                createdEvent.getLocationId(),
                createdEvent.getStatus()
        );
    }

    public EventEntity toEntityFromDomain(Event event) {
        return new EventEntity(
                event.id(),
                event.name(),
                event.ownerId(),
                event.maxPlaces(),
                event.occupiedPlaces(),
                event.registrationList().stream()
                        .map(it -> eventRegistrationMapper.toEntity(it))
                        .toList(),
                event.date(),
                event.cost(),
                event.duration(),
                event.locationId(),
                event.status()
        );
    }

    public Event toDomainFromDto(EventDto event) {
return null;
    }
}
