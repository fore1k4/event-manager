package com.example.event_manager.events.api;

import com.example.event_manager.events.domain.EventRegistrationService;
import com.example.event_manager.security.jwt.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/registration")
public class EventRegistrationController {

    private static Logger logger = LoggerFactory.getLogger(EventRegistrationController.class);
    private final EventRegistrationService eventRegistrationService;
    private final AuthenticationService authenticationService;
    private final EventDtoMapper eventDtoMapper;

    public EventRegistrationController(
            EventRegistrationService eventRegistrationService,
            AuthenticationService authenticationService,
            EventDtoMapper eventDtoMapper
    ) {
        this.eventRegistrationService = eventRegistrationService;
        this.authenticationService = authenticationService;
        this.eventDtoMapper = eventDtoMapper;
    }

    @PostMapping("/{eventId}")
    public ResponseEntity<Void> registerOnEvent(
           @PathVariable("eventId") Long eventId
    ) {
     logger.info("Registering onEvent {}", eventId);

     var currentUser = authenticationService.getCurrentAuthenticatedUser();

     eventRegistrationService.registerOnEvent(currentUser, eventId);

     return ResponseEntity.ok().build();
    }

    @DeleteMapping("/cancelling/{id}")
    public ResponseEntity<Void> cancelRegistrationOnEvent(
          @PathVariable("id")  Long eventId
    ) {
        logger.info("Request for canceling onEvent {}", eventId);

        eventRegistrationService.cancelRegistrationOnEvent(eventId);

        return ResponseEntity.ok().build();
    }


    @GetMapping("/registrations/my")
    public ResponseEntity<List<EventDto>> getUserRegisteredEvents(
    ) {
        logger.info("Request for getting user registration events");

        var events = eventRegistrationService.getUserRegisteredEvents().stream()
                .map(eventDtoMapper::toDto)
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(events);
    }

}
