package com.example.event_manager.events.api;

import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.ZonedDateTime;

public record EventRequestDto(
        @NotBlank(message = "Name is mandatory")
        String name,

        @Positive(message = "Maximum places must be greater than zero")
        Long maxPlaces,

        @Positive(message = "Maximum places must be greater than zero")
        Long occupiedPlaces,

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        ZonedDateTime date,

        @PositiveOrZero(message = "Cost must be non-negative")
        Long cost,


        // Для тестирования статуса событий убираю ограничения
        @Min(value = 30, message = "Duration must be greater than 30")
        Long duration,

        @NotNull(message = "Location ID is mandatory")
        Long locationId
) {
}
