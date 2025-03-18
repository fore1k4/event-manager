package com.example.event_manager.events.database;

import com.example.event_manager.events.domain.Event;
import com.example.event_manager.events.domain.EventDomainMapper;
import com.example.event_manager.events.domain.EventRegistration;
import com.example.event_manager.events.domain.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class EventEntityMapper {


    private  EventService eventService;

    @Autowired
    @Lazy
    private  EventDomainMapper eventDomainMapper;



    public Event toDomain (EventEntity event) {
        return new Event(
               event.getId(),
                event.getName(),
                event.getOwnerId(),
                event.getPlaces(),
                event.getOccupiedPlaces(),
                event.getRegistrationList().stream()
                        .map(it ->
                                new EventRegistration(
                                        it.getId(),
                                        it.getUserId(),
                                        event.getId())
                                        )
                        .toList(),
                event.getDate(),
                event.getCost(),
                event.getDuration(),
                event.getLocationId(),
                event.getStatus()
        );

    }

    public EventEntity toEntity (Event event) {
        return new EventEntity(
                event.id(),
                event.name(),
                event.ownerId(),
                event.maxPlaces(),
                event.occupiedPlaces(),
                event.registrationList().stream()
                        .map(it ->
                                new EventRegistrationEntity(
                                        it.id(),
                                        it.userId(),
                                        eventDomainMapper.toEntityFromDomain(
                                                eventService.getEventById(it.eventId())
                                        )
                        ))
                        .toList(),
                event.date(),
                event.cost(),
                event.duration(),
                event.locationId(),
                event.status()
        );
    }
}
