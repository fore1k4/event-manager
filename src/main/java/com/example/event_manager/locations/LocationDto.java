package com.example.event_manager.locations;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LocationDto(
        Long id,
        @NotBlank
        String name,
        @NotBlank
        @Size(min = 3, message = "Длина строки должна быть не менее 3 символов.")
        String address,
        @Min(value = 5, message = "Локация должна вмещать не менее 5 человек.")
        Long capacity,
        @NotBlank
        @Size(min = 3, message = "Длина строки должна быть не менее 3 символов.")
        String description
) {
}
