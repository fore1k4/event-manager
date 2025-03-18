package com.example.event_manager.locations.api;

import com.example.event_manager.locations.domain.Location;
import org.springframework.stereotype.Component;

@Component
public class LocationDtoConverter {
    public Location toDomain(LocationDto locationDto) {
        return new Location(
                locationDto.id(),
                locationDto.name(),
                locationDto.address(),
                locationDto.capacity(),
                locationDto.description()
        );
    }

    public LocationDto toDto(Location location) {
        return new LocationDto(
                location.id(),
                location.name(),
                location.address(),
                location.capacity(),
                location.description()
        );
    }
}
