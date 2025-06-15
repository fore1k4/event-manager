package com.example.event_manager.events.domain;

import com.example.event_manager.events.EventStatus;
import com.example.event_manager.events.api.EventRequestDto;
import com.example.event_manager.events.api.EventRequestForUpdateDto;
import com.example.event_manager.events.api.SearchFilter;
import com.example.event_manager.events.database.*;
import com.example.event_manager.events.eventKafka.EventChangeMessage;
import com.example.event_manager.events.eventKafka.EventFieldChange;
import com.example.event_manager.events.eventKafka.EventFieldChangeUtil;
import com.example.event_manager.events.eventKafka.EventKafkaSender;
import com.example.event_manager.locations.domain.LocationService;
import com.example.event_manager.security.jwt.AuthenticationService;
import com.example.event_manager.users.database.UserRepository;
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

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final AuthenticationService authenticationService;
    private final LocationService locationService;
    private final EventKafkaSender eventKafkaSender;
    private final EventRegistrationService eventRegistrationService;
    private final UserRepository userRepository;
    private final EventDomainMapper eventDomainMapper;

    public Event createEvent(
            EventRequestDto eventRequestDto
    ) {
        //меньше логов, в будущем если у тебя нагрузка будет 100rps как быстро засрется лог система?
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

        var currentUser = authenticationService.getCurrentAuthenticatedUser();
        String token = authenticationService.getCurrentUserJwtToken();

        eventKafkaSender.sendEvent(new EventChangeMessage(
                id,
                currentUser.id(),
                event.ownerId(),
                eventRegistrationService.getUsersIdFromEvent(event.id()),
                token,
                null,
                null,
                null,
                null,
                null,
                null,
                EventFieldChangeUtil.of(
                        EventStatus.valueOf(event.status().name()),
                        EventStatus.valueOf(EventStatus.CANCELLED.name())
                )
        ));
    }

    public Event updateEvent(
            Long id,
            EventRequestForUpdateDto eventToUpdate
    ) {
        log.debug("Updating event with id {}", id);

        var currentUser = authenticationService.getCurrentAuthenticatedUser();
        String token = authenticationService.getCurrentUserJwtToken();

        var event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + id + " not found"));

        if (!Objects.equals(currentUser.id(), event.getOwnerId())) {
            throw new IllegalArgumentException("Current user is not the owner of the event");
        }

        // Сохраняем копии старых значений для сравнения
        String oldName = event.getName();
        Long oldMaxPlaces = event.getPlaces();
        Long oldOccupiedPlaces = event.getOccupiedPlaces();
        ZonedDateTime oldDate = event.getDate();
        Long oldCost = event.getCost();
        Long oldDuration = event.getDuration();
        Long oldLocationId = event.getLocationId();
        String oldStatus = event.getStatus();

        // Обновляем поля у существующего объекта
        event.setName(eventToUpdate.name());
        event.setPlaces(eventToUpdate.maxPlaces());
        event.setOccupiedPlaces(eventToUpdate.occupiedPlaces());
        event.setDate(eventToUpdate.date());
        event.setCost(eventToUpdate.cost());
        event.setDuration(eventToUpdate.duration());
        event.setLocationId(eventToUpdate.locationId());
        event.setStatus(EventStatus.WAIT_START.name());

        eventRepository.save(event);

        var savedEvent = eventDomainMapper.toDomainFromEntity(event);

        List<Long> userIds = eventRegistrationService.getUsersIdFromEvent(event.getId());

        EventChangeMessage changeMessage = new EventChangeMessage(
                id,
                currentUser.id(),
                event.getOwnerId(),
                userIds,
                token,
                EventFieldChangeUtil.of(oldName, event.getName()),
                EventFieldChangeUtil.of(oldMaxPlaces, event.getPlaces()),
                EventFieldChangeUtil.of(oldDate, event.getDate()),
                EventFieldChangeUtil.of(oldCost, event.getCost()),
                EventFieldChangeUtil.of(oldDuration, event.getDuration()),
                EventFieldChangeUtil.of(oldLocationId, event.getLocationId()),
                EventFieldChangeUtil.of(
                        EventStatus.valueOf(oldStatus),
                        EventStatus.valueOf(event.getStatus())
                )
        );

        log.debug("\uD83D\uDD01 oldName: {}", oldName);
        log.debug("\uD83C\uDD95 newName: {}", event.getName());

        eventKafkaSender.sendEvent(changeMessage);

        return savedEvent;
    }



    public EventChangeMessage toEventChangeMessage(
            Long eventId,
            Event oldEvent,
            EventRequestForUpdateDto newData,
            Long changerId,
            String token
    ) {
        return new EventChangeMessage(
                eventId,
                changerId,
                oldEvent.ownerId(),
                oldEvent.registrationList().stream()
                        .map(eventRegistration -> eventRegistration.userId())
                        .toList(),
                token,
                new EventFieldChange<>(oldEvent.name(), newData.name()),
                new EventFieldChange<>(oldEvent.maxPlaces(), newData.maxPlaces()),
                new EventFieldChange<>(oldEvent.date(), newData.date()),
                new EventFieldChange<>(oldEvent.cost(), newData.cost()),
                new EventFieldChange<>(oldEvent.duration(), newData.duration()),
                new EventFieldChange<>(oldEvent.locationId(), newData.locationId()),
                new EventFieldChange<>(oldEvent.status(), oldEvent.status())
        );
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

        // 1. Получаем событие
        var event = getEventById(eventId);

        // 2. Получаем данные аутентификации (с проверкой на null)
        Long changerId = null;
        String token = null;

        try {
            var currentUser = authenticationService.getCurrentAuthenticatedUser();
            if (currentUser != null) {
                changerId = currentUser.id();
                token = authenticationService.getCurrentUserJwtToken();
            }
        } catch (IllegalStateException e) {
            log.error("No authentication context available - system initiated change");
        }


        // 3. Обновляем статус в БД
        eventRepository.updateEventStatus(eventId, newStatus);

        // 4. Отправляем событие в Kafka
        eventKafkaSender.sendEvent(new EventChangeMessage(
                eventId,
                changerId,  // Теперь передаем реальный changerId или null
                event.ownerId(),
                eventRegistrationService.getUsersIdFromEvent(eventId),
                token,      // Токен или null
                null,       // name
                null,       // maxPlaces
                null,       // date
                null,       // cost
                null,       // duration
                null,       // locationId
                EventFieldChangeUtil.of(
                        EventStatus.valueOf(event.status().name()),
                        EventStatus.valueOf(newStatus)
                )
        ));
    }

    public void cancelEvent(
            Long eventId
    ) {
        log.info("Event cancelling");

        var currentUser = authenticationService.getCurrentAuthenticatedUser();
        var event = getEventById(eventId);
        var newStatus = EventStatus.CANCELLED.name();
        eventRepository.updateEventStatus(eventId, newStatus);
        String token = authenticationService.getCurrentUserJwtToken();
        eventKafkaSender.sendEvent(new EventChangeMessage(
                eventId,
                currentUser.id(),
                event.ownerId(),
                eventRegistrationService.getUsersIdFromEvent(eventId),
                token,
                null,
                null,
                null,
                null,
                null,
                null,
                EventFieldChangeUtil.of(
                        EventStatus.valueOf(event.status().name()),
                        EventStatus.valueOf(newStatus)
                )
        ));
    }

    public List<Event> getCreatedUserEvent() {
        log.info("Retrieving all created user events");

        var currentUser = authenticationService.getCurrentAuthenticatedUser();

        return eventRepository.getCreatedUserEvents(currentUser.id()).stream()
                .map(event -> eventDomainMapper.toDomainFromEntity(event))
                .toList();
    }
}
