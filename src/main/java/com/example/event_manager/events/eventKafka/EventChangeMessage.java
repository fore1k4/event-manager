package com.example.event_manager.events.eventKafka;

import com.example.event_manager.events.EventStatus;

import java.time.ZonedDateTime;
import java.util.List;

public record EventChangeMessage(
        Long eventId,

        Long changerId,

        Long ownerId,

        List<Long> users,

        String token,

        EventFieldChange<String> name,

        EventFieldChange<Long> maxPlaces,

        EventFieldChange<ZonedDateTime> date,

        EventFieldChange<Long> cost,

        EventFieldChange<Long> duration,

        EventFieldChange<Long> locationId,

        EventFieldChange<EventStatus> status
) {}
