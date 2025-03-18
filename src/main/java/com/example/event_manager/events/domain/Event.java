package com.example.event_manager.events.domain;

import com.example.event_manager.events.EventStatus;

import java.time.ZonedDateTime;
import java.util.List;

public record Event(
        Long id,

        String name,

        Long ownerId,

        Long maxPlaces,

        Long occupiedPlaces,

        List<EventRegistration> registrationList,

        ZonedDateTime date,

        Long cost,

        Long duration,

        Long locationId,

        EventStatus status
) {
}
