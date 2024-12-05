package com.example.event_manager.Locations;

import com.example.event_manager.Events.EventConverterDto;
import com.example.event_manager.Events.EventConverterEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LocationConverterDto {
//    public LocationConverterDto() {
//    }
//    @Autowired
//    public void setEventConverterDto(EventConverterDto eventConverterDto) {
//        this.eventConverterDto = eventConverterDto;
//    }
    @Autowired
    private EventConverterDto eventConverterDto;
    public Location toDomain(LocationDto locationDto) {
        return new Location(
               locationDto.id(),
                locationDto.address(),
                locationDto.capacity(),
                locationDto.description(),
                locationDto.eventsDto() == null
                  ?      List.of()
                  :      locationDto.eventsDto().stream()
                        .map(eventDto -> eventConverterDto.toDomain(eventDto))
                        .toList()
        );
    }

    public LocationDto toDto(Location location) {
        return new LocationDto(
                location.id(),
                location.address(),
                location.capacity(),
                location.description(),
                location.events() == null
                ? List.of()
                :location.events().stream()
                        .map(event -> eventConverterDto.toDto(event))
                        .toList()
        );
    }
}
