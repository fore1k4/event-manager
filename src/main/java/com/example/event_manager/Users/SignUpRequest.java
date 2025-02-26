package com.example.event_manager.Users;

public record SignUpRequest(
        String login,
        String password
) {
}
