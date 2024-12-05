package com.example.event_manager.Locations;

import com.example.event_manager.Events.Event;

import java.util.List;

public record Location(

        Integer id,
        String address,
        Integer capacity,
        String description,
        List<Event> events

) {

}
