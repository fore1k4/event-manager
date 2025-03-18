package com.example.event_manager.events.domain;

import com.example.event_manager.events.EventStatus;
import com.example.event_manager.events.api.EventRequestDto;
import com.example.event_manager.events.api.EventRequestForUpdateDto;
import com.example.event_manager.events.api.SearchFilter;
import com.example.event_manager.events.database.*;
import com.example.event_manager.security.jwt.AuthenticationService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {
    private static Logger logger = LoggerFactory.getLogger(EventService.class);
    private final EventRepository eventRepository;
    private final AuthenticationService authenticationService;
    @Autowired
    @Lazy
    private EventDomainMapper eventDomainMapper;
    private final EventRegistrationRepository eventRegistrationRepository;

    public EventService(EventRepository eventRepository, AuthenticationService authenticationService, EventRegistrationRepository eventRegistrationRepository, EventRegistrationMapper eventRegistrationMapper) {
        this.eventRepository = eventRepository;
        this.authenticationService = authenticationService;
        this.eventRegistrationRepository = eventRegistrationRepository;
    }

    public Event createEvent(
            EventRequestDto eventRequestDto
    ) {
        logger.info("Creating new event");
        if (eventRepository.existsByName(eventRequestDto.name())) {
            logger.error("Event with name {} already exists", eventRequestDto.name());
            throw new EntityExistsException("Event with name " + eventRequestDto.name() + " already exists");
        }
        var currentUser = authenticationService.getCurrentAuthenticatedUser();

        if (eventRequestDto.maxPlaces() < eventRequestDto.occupiedPlaces()) {
            throw new IllegalArgumentException("Event with name " + eventRequestDto.name() + " already occupied");
        }
        var createdEvent = new EventEntity(
                null,
                eventRequestDto.name(),
                currentUser.id(),
                eventRequestDto.maxPlaces(),
                eventRequestDto.occupiedPlaces(),
                List.of(),
                eventRequestDto.date(),
                eventRequestDto.cost(),
                eventRequestDto.duration(),
                eventRequestDto.locationId(),
                EventStatus.WAIT_START
        );
        eventRepository.save(createdEvent);

        return eventDomainMapper.toDomainFromEntity(createdEvent);

    }


    public List<Event> getAllEvents() {
        logger.info("Retrieving all events");
        return eventRepository.findAll()
                .stream()
                .map(eventDomainMapper::toDomainFromEntity)
                .toList();
    }

    public List<Event> getEventsByUserId(
            Long userId
    ) {
        var events = eventRepository.findEventsByOwnerId(userId).stream()
                .map(it -> eventDomainMapper.toDomainFromEntity(it))
                .toList();

        for (int i = 0; i < events.size(); i++) {
            System.out.println(events.get(i));
        }

        return events;
    }

    public void deleteEventById(
            Long id
    ) {
        logger.info("Deleting event with id {}", id);
        if (!eventRepository.existsById(id)) {
            throw new EntityNotFoundException("Event with id " + id + " not found");
        }
        eventRepository.deleteById(id);
    }

    public Event updateEvent(
            Long id,
            EventRequestForUpdateDto eventToUpdate
    ) {
        logger.info("Updating event with id {}", id);
        var currentUser = authenticationService.getCurrentAuthenticatedUser();

        var event = eventRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Event with id " + id + " not found")
                );

        var updatedEvent = new EventEntity(
                event.getId(),
                eventToUpdate.name(),
                currentUser.id(),
                eventToUpdate.maxPlaces(),
                eventToUpdate.occupiedPlaces(),
                event.getRegistrationList(),
                eventToUpdate.date(),
                eventToUpdate.cost(),
                eventToUpdate.duration(),
                eventToUpdate.locationId(),
                EventStatus.WAIT_START
        );

        eventRepository.save(updatedEvent);

        return eventDomainMapper.toDomainFromEntity(updatedEvent);
    }

    public Long getPlaces(
            Long eventId
    ) {
        return eventRepository.getMaxPlaces(eventId);
    }

    public Long getOccupiedPlaces(
            Long eventId
    ) {
        return eventRepository.getOccupiedPlaces(eventId);
    }


    public Event getEventById(
            Long id
    ) {
        logger.info("Retrieving event with id {}", id);
        var event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + id + " not found"));

        return eventDomainMapper.toDomainFromEntity(event);
    }

    public List<Event> getEventsWithFilters(
            SearchFilter searchFilter
    ) {
        var foundEntities = eventRepository.findEvents(
                searchFilter.name(),
                searchFilter.ownerId(),
                searchFilter.maxPlaces(),
                searchFilter.minPlaces(),
                searchFilter.minCost(),
                searchFilter.maxCost(),
                searchFilter.timeBefore(),
                searchFilter.timeAfter(),
                searchFilter.status(),
                searchFilter.minDuration(),
                searchFilter.maxDuration(),
                searchFilter.locationId()
        );

        return foundEntities.stream()
                .map(it ->
                        eventDomainMapper.toDomainFromEntity(it))
                .toList();

    }

    public void updateStatus(
            Long eventId,
            EventStatus newStatus
    ) {
        logger.info("Updating event status");

        eventRepository.updateEventStatus(eventId, newStatus);
    }

    public void eventCancel(
            Long eventId
    ) {
        logger.info("Event cancelled");

        eventRepository.updateEventStatus(eventId, EventStatus.CANCELLED);
    }

    public List<Event> getUserRegisteredEvents() {
        var currentUser = authenticationService.getCurrentAuthenticatedUser();

        var eventsId = eventRegistrationRepository.findEventsByUserId(currentUser.id())
                .orElseThrow(() -> new EntityNotFoundException("User with id " + currentUser.id() + " not found"));


        return eventRepository.findAllByEventId(eventsId)
                .stream().map(event -> eventDomainMapper.toDomainFromEntity(event))
                .toList();
    }

}
