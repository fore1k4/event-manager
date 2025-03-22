package com.example.event_manager.events.domain;

import com.example.event_manager.events.EventStatus;
import com.example.event_manager.events.api.EventRequestDto;
import com.example.event_manager.events.api.EventRequestForUpdateDto;
import com.example.event_manager.events.api.SearchFilter;
import com.example.event_manager.events.database.*;
import com.example.event_manager.locations.domain.LocationService;
import com.example.event_manager.security.jwt.AuthenticationService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class EventService {

    private static Logger logger = LoggerFactory.getLogger(EventService.class);

    private final EventRepository eventRepository;
    private final AuthenticationService authenticationService;
    private final LocationService locationService;
    @Autowired
    @Lazy
    private EventDomainMapper eventDomainMapper;

    public EventService(EventRepository eventRepository, AuthenticationService authenticationService, LocationService locationService) {
        this.eventRepository = eventRepository;
        this.authenticationService = authenticationService;
        this.locationService = locationService;
    }

    public Event createEvent(
            EventRequestDto eventRequestDto
    ) {
        logger.info("Creating new event");

        if (eventRepository.existsByName(eventRequestDto.name())) {
            logger.error("Event with name {} already exists", eventRequestDto.name());
            throw new EntityExistsException("Event with name " + eventRequestDto.name() + " already exists");
        }

        var location = locationService.getLocationById(eventRequestDto.locationId());
        if (location.capacity() < eventRequestDto.maxPlaces()) {
            throw new IllegalArgumentException("Location with id " + eventRequestDto.locationId() + " smallest than " + eventRequestDto.maxPlaces() + " places");
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
                EventStatus.WAIT_START.name()
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

        if (!Objects.equals(currentUser.id(), event.getOwnerId()))  {
            throw new IllegalArgumentException("Current user is not the owner of the event");
        }

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
                EventStatus.WAIT_START.name()
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
            String newStatus
    ) {
        logger.info("Updating event status");

        eventRepository.updateEventStatus(eventId, newStatus);
    }

    public void cancelEvent(
            Long eventId
    ) {
        logger.info("Event cancelled");

        eventRepository.updateEventStatus(eventId, EventStatus.CANCELLED.name());
    }

    public List<Event> getCreatedUserEvent() {
        logger.info("Retrieving all created user events");

        var currentUser = authenticationService.getCurrentAuthenticatedUser();

        return eventRepository.getCreatedUserEvents(currentUser.id()).stream()
                .map(event -> eventDomainMapper.toDomainFromEntity(event))
                .toList();
    }



}
