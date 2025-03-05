package com.example.event_manager.exceptionsHandler;

import java.time.LocalDateTime;

public record ServerErrorDto(
        String message,
        String detailedMessage,
        LocalDateTime time
) {
}
