package com.example.event_manager.users.api;

public record SignInRequest(
        String login,
        String password
) {
}
