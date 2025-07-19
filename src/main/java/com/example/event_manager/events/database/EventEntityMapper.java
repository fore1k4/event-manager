package com.example.event_manager.events.database;

import com.example.event_manager.events.EventStatus;
import com.example.event_manager.events.domain.Event;
import com.example.event_manager.events.domain.EventDomainMapper;
import com.example.event_manager.events.domain.EventRegistration;
import com.example.event_manager.events.domain.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class EventEntityMapper {

    @Autowired
    private EventRepository eventRepository;

    public EventEntity toEntity(Event event) {
        return new EventEntity(
                event.id(),
                event.name(),
                event.ownerId(),
                event.maxPlaces(),
                event.occupiedPlaces(),
                event.registrationList().stream()
                        .map(it -> {
                            var eventById = eventRepository.findById(it.eventId()).get();
                            return new EventRegistrationEntity(
                                    it.id(),
                                    it.userId(),
                                    eventById
                            );
                        })
                        .toList(),
                event.date(),
                event.cost(),
                event.duration(),
                event.locationId(),
                event.status().name()
        );
    }
}
