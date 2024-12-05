package com.example.event_manager.Events;

import com.example.event_manager.Locations.Location;
import com.example.event_manager.Locations.LocationDto;

import java.util.List;

public record EventDto(
        Integer id,
        String name,
        Integer ownerId,
        Integer maxPlaces,
        Integer occupiedPlaces,
        String date,
        Integer duration,
        Integer locationDtoId
){
}
