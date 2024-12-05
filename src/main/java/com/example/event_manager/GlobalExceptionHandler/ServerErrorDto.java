package com.example.event_manager.GlobalExceptionHandler;

import java.time.LocalDateTime;

public record ServerErrorDto(
        String message,
        String detailedMessage,
        LocalDateTime time
) {
}
