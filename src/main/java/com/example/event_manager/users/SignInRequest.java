package com.example.event_manager.users;

public record SignInRequest(
        String login,
        String password
) {
}
