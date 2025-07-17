package com.example.event_manager.events.domain;

import com.example.event_manager.events.EventStatus;
import com.example.event_manager.events.api.EventRequestDto;
import com.example.event_manager.events.api.EventRequestForUpdateDto;
import com.example.event_manager.events.api.SearchFilter;
import com.example.event_manager.events.database.*;
import com.example.event_manager.events.eventKafka.EventChangeMessage;
import com.example.event_manager.events.eventKafka.EventFieldChange;
import com.example.event_manager.locations.domain.LocationService;
import com.example.event_manager.notifications.NotificationService;
import com.example.event_manager.security.jwt.AuthenticationService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final AuthenticationService authenticationService;
    private final LocationService locationService;
    private final EventDomainMapper eventDomainMapper;
    private final NotificationService notificationService;

    public Event createEvent(
            EventRequestDto eventRequestDto
    ) {
        log.debug("Creating new event");

        if (eventRepository.existsByName(eventRequestDto.name())) {
            log.error("Event with name {} already exists", eventRequestDto.name());
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

        var savedEvent = eventDomainMapper.toDomainFromEntity(createdEvent);

        return savedEvent;
    }


    public List<Event> getAllEvents() {
        log.debug("Retrieving all events");
        return eventRepository.findAll()
                .stream()
                .map(eventDomainMapper::toDomainFromEntity)
                .toList();
    }

    public void deleteEventById(
            Long id
    ) {
        log.debug("Deleting event with id {}", id);
        if (!eventRepository.existsById(id)) {
            throw new EntityNotFoundException("Event with id " + id + " not found");
        }

        var event = getEventById(id);

        eventRepository.deleteById(id);

        notificationService.deleteEvent(event);
    }

    public Event updateEvent(Long id, EventRequestForUpdateDto eventToUpdate) {
        log.debug("Updating event with id {}", id);

        var currentUser = authenticationService.getCurrentAuthenticatedUser();
        EventEntity eventEntity = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + id + " not found"));

        if (!Objects.equals(currentUser.id(), eventEntity.getOwnerId())) {
            throw new IllegalArgumentException("Current user is not the owner of the event");
        }

        Event oldEvent = eventDomainMapper.toDomainFromEntity(eventEntity);

        eventEntity.setName(eventToUpdate.name());
        eventEntity.setPlaces(eventToUpdate.maxPlaces());
        eventEntity.setOccupiedPlaces(eventToUpdate.occupiedPlaces());
        eventEntity.setDate(eventToUpdate.date());
        eventEntity.setCost(eventToUpdate.cost());
        eventEntity.setDuration(eventToUpdate.duration());
        eventEntity.setLocationId(eventToUpdate.locationId());
        eventEntity.setStatus(EventStatus.WAIT_START.name());

        eventRepository.save(eventEntity);

        Event updatedEvent = eventDomainMapper.toDomainFromEntity(eventEntity);

        notificationService.sendEventUpdate(oldEvent, updatedEvent);

        return updatedEvent;
    }



    @Transactional
    public Event getEventById(
            Long id
    ) {
        log.debug("Retrieving event with id {}", id);
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

    public void updateStatus(Long eventId, String newStatus) {
        log.debug("Updating event status for event {}", eventId);

        var event = getEventById(eventId);



        eventRepository.updateEventStatus(eventId, newStatus);

       notificationService.updateStatus(event, newStatus);
    }

    public void cancelEvent(
            Long eventId
    ) {
        log.info("Event cancelling");

        var event = getEventById(eventId);
        var newStatus = EventStatus.CANCELLED.name();
        eventRepository.updateEventStatus(eventId, newStatus);
        notificationService.cancelEvent(event);
    }

    public List<Event> getCreatedUserEvent() {
        log.info("Retrieving all created user events");

        var currentUser = authenticationService.getCurrentAuthenticatedUser();

        return eventRepository.getCreatedUserEvents(currentUser.id()).stream()
                .map(event -> eventDomainMapper.toDomainFromEntity(event))
                .toList();
    }
}
