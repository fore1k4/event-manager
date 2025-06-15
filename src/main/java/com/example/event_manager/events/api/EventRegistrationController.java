package com.example.event_manager.events.api;

import com.example.event_manager.events.domain.EventRegistrationService;
import com.example.event_manager.security.jwt.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/registration")
@RequiredArgsConstructor
public class EventRegistrationController {

    private final EventRegistrationService eventRegistrationService;
    private final AuthenticationService authenticationService;
    private final EventDtoMapper eventDtoMapper;

    @PostMapping("/{eventId}")
    public ResponseEntity<Void> registerOnEvent(
           @PathVariable("eventId") Long eventId
    ) {
     log.debug("Registering onEvent {}", eventId);

     var currentUser = authenticationService.getCurrentAuthenticatedUser();
     eventRegistrationService.registerOnEvent(currentUser, eventId);

     return ResponseEntity.ok().build();
    }

    @DeleteMapping("/cancelling/{id}")
    public ResponseEntity<Void> cancelRegistrationOnEvent(
          @PathVariable("id")  Long eventId
    ) {
        log.debug("Request for canceling onEvent {}", eventId);

        eventRegistrationService.cancelRegistrationOnEvent(eventId);

        return ResponseEntity.ok().build();
    }


    @GetMapping("/registrations/my")
    public ResponseEntity<List<EventDto>> getUserRegisteredEvents(
    ) {
        log.debug("Request for getting user registration events");

        var events = eventRegistrationService.getUserRegisteredEvents().stream()
                .map(eventDtoMapper::toDto)
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(events);
    }

}
