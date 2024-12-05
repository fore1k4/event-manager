package com.example.event_manager.Events;

import com.example.event_manager.Locations.*;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final EventConverterEntity eventConverterEntity;
    private final LocationConverterEntity locationConverterEntity;
    private final LocationService locationService;
    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    public EventService(
            EventRepository eventRepository,
            EventConverterEntity eventConverterEntity,
            LocationConverterEntity locationConverterEntity,
            LocationService locationService) {
        this.eventRepository = eventRepository;
        this.eventConverterEntity = eventConverterEntity;
        this.locationConverterEntity = locationConverterEntity;
        this.locationService = locationService;
    }

    public Event createEvent(
            Event event
    ) {
        log.info("create event process with id = {}",event.id());
        if(eventRepository.existsByName(event.name())) {
            throw new EntityExistsException("event with name = %s, already exists"
                    .formatted(event.name()));
        }
        var createdEvent = eventRepository.save(eventConverterEntity.toEntity(event));

        return eventConverterEntity.toDomain(createdEvent);
    }

    public List<Event> getAllEvents() {
        log.info("get all event process");
        return eventRepository.findAll().stream()
                .map(eventConverterEntity::toDomain)
                .toList();
    }

    public Event getEventById(
            Integer id
    ) {
        log.info("get event process with id = {}",id);
        if(!eventRepository.existsById(id)) {
            throw new EntityNotFoundException("event with id = %s, not found"
                    .formatted(id));
        }
       return eventConverterEntity.toDomain(eventRepository.findById(id).orElseThrow());
    }

    public void deleteEvent(
            Integer id
    ) {
        log.info("delete event process with id = {}",id);
        var eventToDelete = getEventById(id);
        eventRepository.delete(eventConverterEntity.toEntity(eventToDelete));
    }

    public Event updateEvent(
            Integer id,
            Event eventToUpdate
    ) {
        log.info("update event process with id = {}",id);
        var existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event with id = %s not found"
                        .formatted(id)));

        LocationEntity location = null;
        if (eventToUpdate.locationId() != null) {
            location = locationService.getLocationByIdEntity(eventToUpdate.locationId());
        }


        existingEvent.setName(eventToUpdate.name());
        existingEvent.setOwnerId(eventToUpdate.ownerId());
        existingEvent.setMaxPlaces(eventToUpdate.maxPlaces());
        existingEvent.setOccupiedPlaces(eventToUpdate.occupiedPlaces());
        existingEvent.setDate(eventToUpdate.date());
        existingEvent.setDuration(eventToUpdate.duration());
        existingEvent.setLocation(location);


        var updatedEvent = eventRepository.save(existingEvent);

        return eventConverterEntity.toDomain(updatedEvent);
    }

}
