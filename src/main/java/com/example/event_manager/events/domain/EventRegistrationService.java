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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventRegistrationService {

    private final EventRegistrationRepository eventRegistrationRepository;
    private final EventEntityMapper eventEntityMapper;
    private final EventRepository eventRepository;
    private final AuthenticationService authenticationService;
    private final EventDomainMapper eventDomainMapper;


    @Transactional
    public void registerOnEvent(
            User user,
            Long eventId
    ) {

        log.info("Registering event");
        log.info("Dependency check - eventRepository: {}, eventDomainMapper: {}, eventRegistrationRepository: {}",
                eventRepository != null,
                eventDomainMapper != null,
                eventRegistrationRepository != null);

        var event = eventDomainMapper.toDomainFromEntity(eventRepository.findById(eventId)
                .orElseThrow());


        if (eventRegistrationRepository.existsUserByEventId(user.id())) {
            log.error("User already registered");
            throw new EntityExistsException("User with id " + user.id() + " already registered");
        }


        if (!event.status().equals(EventStatus.WAIT_START)) {
            log.error("Event is not waiting start");
            throw new IllegalArgumentException("Cannot register on Event " + eventId);
        }


        var registration = eventRegistrationRepository.findRegistration(user.id(), eventId);
        if (registration.isPresent()) {
            log.error("User already registered");
            throw new EntityExistsException("User with id " + user.id() + " already registered");
        }

        var eventForCheck = eventRepository.findById(eventId).orElseThrow();

        if (eventForCheck.getOccupiedPlaces() >= eventForCheck.getPlaces()) {
            throw new IllegalArgumentException("Event with id " + eventId + " shouldn't get for registration");
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
        log.info("Registration cancelling");

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

    public List<Long> getUsersIdFromEvent(
            Long eventId
    ) {
       return eventRegistrationRepository.findUserIdsByEventId(eventId).orElseThrow(
               () -> new EntityNotFoundException("Event with id " + eventId + " not found")
       );



    }
}
