package com.example.event_manager.events.api;

import com.example.event_manager.events.EventStatus;

import java.time.ZonedDateTime;

public record SearchFilter(
        String name,
        Long ownerId,
        Long maxPlaces,
        Long minPlaces,
        Long minCost,
        Long maxCost,
        ZonedDateTime timeBefore,
        ZonedDateTime timeAfter,
        EventStatus status,
        Long minDuration,
        Long maxDuration,
        Long locationId
) {
}
