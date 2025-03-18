package com.example.event_manager.events.api;

import jakarta.validation.constraints.*;

import java.time.ZonedDateTime;

public record EventRequestForUpdateDto(
        @NotNull(message = "ID is mandatory")
        Long id,

        @NotBlank(message = "Name is mandatory")
        String name,

        @Positive(message = "Maximum places must be greater than zero")
        Long maxPlaces,

        @PositiveOrZero(message = "Occupied places must be non-negative")
        Long occupiedPlaces,

        @NotNull(message = "Date is mandatory")
        ZonedDateTime date,

        @PositiveOrZero(message = "Cost must be non-negative")
        Long cost,

        @Min(value = 30, message = "Duration must be at least 30 minutes")
        Long duration,

        @NotNull(message = "Location ID is mandatory")
        Long locationId

) {
}
