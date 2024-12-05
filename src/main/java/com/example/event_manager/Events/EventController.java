package com.example.event_manager.Events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;
    private final EventConverterDto eventConverterDto;
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    public EventController(
            EventService eventService,
            EventConverterDto eventConverterDto
    ) {
        this.eventService = eventService;
        this.eventConverterDto = eventConverterDto;
    }

    @PostMapping
    public ResponseEntity<EventDto> createEvent(
            @RequestBody EventDto eventDto
    ) {
        logger.info("RECEIVED POST request for event = {}",eventDto);
        var createdEvent = eventService.createEvent(eventConverterDto.toDomain(eventDto));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventConverterDto.toDto(createdEvent));
    }

    @GetMapping
    public ResponseEntity<List<EventDto>> getAllEvents() {
        logger.info("RECEIVED GET request");
        var events = eventService.getAllEvents().stream()
                .map(eventConverterDto::toDto)
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(events);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventDto> updateEvent(
            @PathVariable("id") Integer id,
            @RequestBody  EventDto eventToUpdate
    ) {
        logger.info("RECEIVED PUT request for eventId = {}",id);
        var updatedLocation =
                eventService.updateEvent(id,eventConverterDto.toDomain(eventToUpdate));

        return ResponseEntity.status(HttpStatus.OK)
                .body(eventConverterDto.toDto(updatedLocation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable("id") Integer id
    ) {
        logger.info("RECEIVED DELETE request for eventId = {}", id);
        eventService.deleteEvent(id);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDto> getEventById(
            @PathVariable("id") Integer id
    ){
        logger.info("RECEIVED GET request for eventId = {}", id);
        var event = eventService.getEventById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(eventConverterDto.toDto(event));
    }


}
