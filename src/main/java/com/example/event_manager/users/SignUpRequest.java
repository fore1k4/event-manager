package com.example.event_manager.users;

public record SignUpRequest(
        String login,
        String password
) {
}
