package com.example.event_manager.Locations;

import org.springframework.stereotype.Component;

@Component
public class LocationConverterDto {
    public Location toDomain(LocationDto locationDto) {
        return new Location(
               locationDto.id(),
                locationDto.address(),
                locationDto.capacity(),
                locationDto.description()
        );
    }

    public LocationDto toDto(Location location) {
        return new LocationDto(
                location.id(),
                location.address(),
                location.capacity(),
                location.description()
        );
    }
}
