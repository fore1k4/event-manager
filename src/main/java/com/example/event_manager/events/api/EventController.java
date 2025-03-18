package com.example.event_manager.events.api;

import com.example.event_manager.events.domain.EventService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private static Logger logger = LoggerFactory.getLogger(EventController.class);

    private final EventService eventService;
    private final EventDtoMapper eventDtoMapper;

    public EventController(
            EventService eventService,
            EventDtoMapper eventDtoMapper
    ) {
        this.eventService = eventService;
        this.eventDtoMapper = eventDtoMapper;
    }


    @PostMapping
    public ResponseEntity<EventDto> createEvent(
            @RequestBody @Valid EventRequestDto eventRequestDto
    ) {

        logger.info("Request for creating new event {}", eventRequestDto);
        var createdEvent = eventService.createEvent(eventRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventDtoMapper.toDto(createdEvent));
    }

    @GetMapping
    public ResponseEntity<List<EventDto>> getAllEvents() {
        logger.info("Request for getting all events");
        var events = eventService.getAllEvents()
                .stream()
                .map(eventDtoMapper::toDto)
                .toList();
        return ResponseEntity.status(HttpStatus.OK)
                .body(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDto> getEventById(
            @PathVariable("id") Long id
    ) {
        logger.info("Request for getting event with id {}", id);
        var event = eventService.getEventById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(eventDtoMapper.toDto(event));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<EventDto> deleteEvent(
            @PathVariable("id") Long id
    ) {
        logger.info("Request for deleting event with id {}", id);
        eventService.deleteEventById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventDto> updateEvent(
            @PathVariable("id") Long id,
            @RequestBody EventRequestForUpdateDto eventRequestForUpdateDto
    ) {
        logger.info("Request for updating event with id {}", id);

        var updatedEvent = eventService.updateEvent(id, eventRequestForUpdateDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(eventDtoMapper.toDto(updatedEvent));
    }

    @GetMapping("/my/{userId}")
    public ResponseEntity<List<EventDto>> getEventByUserId(
            @PathVariable("userId") Long userId
    ) {
        logger.info("Request for getting events with userId {}", userId);

        var events = eventService.getEventsByUserId(userId).stream()
                .map(it -> eventDtoMapper.toDto(it))
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(events);
    }

    @GetMapping("/search")
    public ResponseEntity<List<EventDto>> searchEvent(
            @RequestBody SearchFilter searchFilter
    ) {
        logger.info("Request for searching events");

        var searchingEvents = eventService.getEventsWithFilters(searchFilter);

        return ResponseEntity.status(HttpStatus.OK)
                .body(searchingEvents.stream()
                        .map(event -> eventDtoMapper.toDto(event))
                        .toList()
                );
    }

    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<Void> cancelEvent(
            @PathVariable("id") Long eventId
    ) {
        logger.info("Request for canceling event");

        eventService.eventCancel(eventId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/registrations/my")
    public ResponseEntity<List<EventDto>> getUserRegisteredEvents(
    ) {
        logger.info("Request for getting user registration events");

        var events = eventService.getUserRegisteredEvents().stream()
                .map(event -> eventDtoMapper.toDto(event))
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(events);
    }


}
