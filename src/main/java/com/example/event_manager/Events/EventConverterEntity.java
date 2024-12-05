package com.example.event_manager.Events;

import com.example.event_manager.Locations.LocationConverterEntity;
import com.example.event_manager.Locations.LocationEntity;
import com.example.event_manager.Locations.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class EventConverterEntity {
//    @Autowired
//    @Lazy
//    private LocationConverterEntity locationConverterEntity;

    public Event toDomain(EventEntity eventEntity) {
        return new Event(
                eventEntity.getId(),
                eventEntity.getName(),
                eventEntity.getOwnerId(),
                eventEntity.getMaxPlaces(),
                eventEntity.getOccupiedPlaces(),
                eventEntity.getDate(),
                eventEntity.getDuration(),
                eventEntity.getLocationId()
               // eventEntity.getLocation() != null ? eventEntity.getLocation().getId() : null
        );
    }

    public EventEntity toEntity(Event event) {
        LocationEntity locationEntity = new LocationEntity();
        locationEntity.setId(event.locationId());
        return new EventEntity(
                event.id(),
                event.name(),
                event.ownerId(),
                event.maxPlaces(),
                event.occupiedPlaces(),
                event.date(),
                event.duration(),
                locationEntity
        );
    }
}

