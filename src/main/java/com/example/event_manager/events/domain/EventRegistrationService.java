package com.example.event_manager.events.domain;

import com.example.event_manager.events.EventStatus;
import com.example.event_manager.events.database.EventEntityMapper;
import com.example.event_manager.events.database.EventRegistrationEntity;
import com.example.event_manager.events.database.EventRegistrationRepository;
import com.example.event_manager.events.database.EventRepository;
import com.example.event_manager.security.jwt.AuthenticationService;
import com.example.event_manager.users.domain.User;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventRegistrationService {

    private static Logger logger = LoggerFactory.getLogger(EventRegistrationService.class);
    private final EventRegistrationRepository eventRegistrationRepository;
    private final EventService eventService;
    private final EventEntityMapper eventEntityMapper;
    private final EventRepository eventRepository;
    private final AuthenticationService authenticationService;
    private final EventDomainMapper eventDomainMapper;


    public EventRegistrationService(EventRegistrationRepository eventRegistrationRepository, EventService eventService, EventEntityMapper eventEntityMapper, EventRepository eventRepository, AuthenticationService authenticationService, EventDomainMapper eventDomainMapper) {
        this.eventRegistrationRepository = eventRegistrationRepository;
        this.eventService = eventService;
        this.eventEntityMapper = eventEntityMapper;
        this.eventRepository = eventRepository;
        this.authenticationService = authenticationService;
        this.eventDomainMapper = eventDomainMapper;
    }


    @Transactional
    public void registerOnEvent(
            User user,
            Long eventId
    ) {

        logger.info("Registering event");
        var event = eventService.getEventById(eventId);


        if (eventRegistrationRepository.existsUserByEventId(user.id())) {
            logger.error("User already registered");
            throw new EntityExistsException("User with id " + user.id() + " already registered");
        }


        if (!event.status().equals(EventStatus.WAIT_START)) {
            logger.error("Event is not waiting start");
            throw new IllegalArgumentException("Cannot register on Event " + eventId);
        }


        var registration = eventRegistrationRepository.findRegistration(user.id(), eventId);
        if (registration.isPresent()) {
            logger.error("User already registered");
            throw new EntityExistsException("User with id " + user.id() + " already registered");
        }


        if (eventService.getOccupiedPlaces(eventId) >= eventService.getPlaces(eventId)) {
            throw new IllegalArgumentException("Event with id " + eventId + " mest nety");
        }


        var eventRegistrationEntity = new EventRegistrationEntity(
                null,
                user.id(),
                eventEntityMapper.toEntity(event)
        );


        eventRegistrationRepository.save(eventRegistrationEntity);
        eventRepository.addPersoneOnEventPlace(eventId);

    }

    public void cancelRegistrationOnEvent(
            Long eventId
    ) {
        logger.info("Registration cancelling");

        var currentUser = authenticationService.getCurrentAuthenticatedUser();

        var eventRegistration = eventRegistrationRepository.findRegistration(currentUser.id(), eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId + " not found"));

        eventRegistrationRepository.delete(eventRegistration);
        eventRepository.minusPersoneOnEventPlace(eventId);
    }

    public List<Event> getUserRegisteredEvents() {
        var currentUser = authenticationService.getCurrentAuthenticatedUser();

        var eventsId = eventRegistrationRepository.findEventsByUserId(currentUser.id())
                .orElseThrow(() -> new EntityNotFoundException("User with id " + currentUser.id() + " not found"));


        return eventRepository.findAllByEventId(eventsId)
                .stream().map(eventDomainMapper::toDomainFromEntity)
                .toList();
    }
}
