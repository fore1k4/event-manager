package com.example.event_manager.Locations;

import com.example.event_manager.Events.Event;
import com.example.event_manager.Events.EventDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

import java.util.List;

public record LocationDto(
        @Null(message = "поле id должно быть null")
        Integer id,
        @NotBlank
        @Size(min = 3, message = "Длина строки должна быть не менее 3 символов.")
        String address,
        @Min(value = 5, message = "Локация должна вмещать не менее 5 человек.")
        Integer capacity,
        @NotBlank
        @Size(min = 3, message = "Длина строки должна быть не менее 3 символов.")
        String description,
        List<EventDto> eventsDto
) {
}
