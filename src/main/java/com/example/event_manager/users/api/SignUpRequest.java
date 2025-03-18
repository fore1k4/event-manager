package com.example.event_manager.users.api;

public record SignUpRequest(
        String login,
        String password
) {
}
