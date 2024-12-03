package com.example.event_manager.Locations;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

public record LocationDto(
        @Null
        Integer id,
        @NotBlank
        @Size(min = 15, message = "Длина строки должна быть не менее 3 символов.")
        String address,
        @Min(value = 5, message = "Локация должна вмещать не менее 5 человек.")
        Integer capacity,
        @NotBlank
        @Size(min = 15, message = "Длина строки должна быть не менее 3 символов.")
        String description
) {
}
