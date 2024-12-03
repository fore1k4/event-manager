package com.example.event_manager.Locations;

import org.springframework.stereotype.Component;

@Component
public class LocationConverterEntity {
    public Location toDomain(LocationEntity locationEntity) {
        return new Location(
                locationEntity.getId(),
                locationEntity.getAddress(),
                locationEntity.getCapacity(),
                locationEntity.getDescription()
        );

    }
    public LocationEntity toEntity (Location location) {
        return new LocationEntity(
                location.id(),
                location.address(),
                location.capacity(),
                location.description()
        );

    }
}
