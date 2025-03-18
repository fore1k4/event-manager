package com.example.event_manager.locations.domain;

public record Location(
        Long id,
        String name,
        String address,
        Long capacity,
        String description

) {

}
