package com.example.event_manager.Locations;

import com.example.event_manager.Events.EventConverterEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LocationConverterEntity {
    @Autowired
    private EventConverterEntity eventConverterEntity;

    public Location toDomain(LocationEntity locationEntity) {
        return new Location(
                locationEntity.getId(),
                locationEntity.getAddress(),
                locationEntity.getCapacity(),
                locationEntity.getDescription(),
                locationEntity.getEvents() == null
                ? List.of()
                : locationEntity.getEvents().stream()
                        .map(eventEntity -> eventConverterEntity.toDomain(eventEntity))
                        .toList()
        );

    }
    public LocationEntity toEntity (Location location) {
        return new LocationEntity(
                location.id(),
                location.address(),
                location.capacity(),
                location.description(),
                location.events() == null
                ?        List.of()
                :        location.events().stream()
                        .map(event -> eventConverterEntity.toEntity(event))
                        .toList()
        );

    }
}
