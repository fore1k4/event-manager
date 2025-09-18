package com.example.event_manager.notifications;

import com.example.event_manager.events.EventStatus;
import com.example.event_manager.events.domain.Event;
import com.example.event_manager.events.domain.EventRegistrationService;
import com.example.event_manager.events.eventKafka.EventChangeMessage;
import com.example.event_manager.events.eventKafka.EventFieldChangeUtil;
import com.example.event_manager.events.eventKafka.EventKafkaSender;
import com.example.event_manager.security.jwt.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final EventKafkaSender eventKafkaSender;
    private final AuthenticationService authenticationService;
    private final EventRegistrationService eventRegistrationService;

    public void sendEventUpdate(Event oldEvent, Event newEvent) {
        var currentUser = authenticationService.getCurrentAuthenticatedUser();
        String token = authenticationService.getCurrentUserJwtToken();

        EventChangeMessage message = new EventChangeMessage(
                newEvent.id(),
                currentUser.id(),
                newEvent.ownerId(),
                eventRegistrationService.getUsersIdFromEvent(newEvent.id()),
                token,
                EventFieldChangeUtil.of(oldEvent.name(), newEvent.name()),
                EventFieldChangeUtil.of(oldEvent.maxPlaces(), newEvent.maxPlaces()),
                EventFieldChangeUtil.of(oldEvent.date(), newEvent.date()),
                EventFieldChangeUtil.of(oldEvent.cost(), newEvent.cost()),
                EventFieldChangeUtil.of(oldEvent.duration(), newEvent.duration()),
                EventFieldChangeUtil.of(oldEvent.locationId(), newEvent.locationId()),
                EventFieldChangeUtil.of(oldEvent.status(), newEvent.status())
        );

        eventKafkaSender.sendEvent(message);
    }

    public void updateStatus(Event event, String newStatus) {
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

        eventKafkaSender.sendEvent(new EventChangeMessage(
                event.id(),
                changerId,
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
                        EventStatus.valueOf(newStatus)
                )
                )
        );

    }

    public void cancelEvent(Event event) {
        var currentUser = authenticationService.getCurrentAuthenticatedUser();
        String token = authenticationService.getCurrentUserJwtToken();

        eventKafkaSender.sendEvent(new EventChangeMessage(
                event.id(),
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

    public void deleteEvent(Event event) {
        var currentUser = authenticationService.getCurrentAuthenticatedUser();
        String token = authenticationService.getCurrentUserJwtToken();

        EventChangeMessage message = new EventChangeMessage(
                event.id(),
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
        );

        eventKafkaSender.sendEvent(message);
    }

}
