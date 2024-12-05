package com.example.event_manager.Events;

import com.example.event_manager.Locations.Location;

import java.time.LocalDateTime;
import java.util.List;

public record Event(
        Integer id,
        String name,
        Integer ownerId,
        Integer maxPlaces,
        Integer occupiedPlaces,
        String date,
        Integer duration,
        Integer locationId

) {
}
