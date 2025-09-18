package com.example.event_manager.events.domain;

import com.example.event_manager.events.EventStatus;
import com.example.event_manager.events.database.EventEntity;
import com.example.event_manager.events.database.EventRegistrationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventDomainMapper {
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
                EventStatus.valueOf(createdEvent.getStatus())
        );
    }
}
