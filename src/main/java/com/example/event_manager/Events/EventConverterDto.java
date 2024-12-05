package com.example.event_manager.Events;

import com.example.event_manager.Locations.Location;
import com.example.event_manager.Locations.LocationConverterDto;
import com.example.event_manager.Locations.LocationDto;
import com.example.event_manager.Locations.LocationEntity;
import jakarta.persistence.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class EventConverterDto {

    public Event toDomain(EventDto event) {
        return new Event(
                event.id(),
                event.name(),
                event.ownerId(),
                event.maxPlaces(),
                event.occupiedPlaces(),
                event.date(),
                event.duration(),
                event.locationDtoId()
        );
    }

    public EventDto toDto(Event event) {
        return new EventDto(
                event.id(),
                event.name(),
                event.ownerId(),
                event.maxPlaces(),
                event.occupiedPlaces(),
                event.date(),
                event.duration(),
                event.locationId()
        );
    }
}
