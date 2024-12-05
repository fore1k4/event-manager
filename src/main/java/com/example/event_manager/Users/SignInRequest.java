package com.example.event_manager.Users;

public record SignInRequest(
        String login,
        String password
) {
}
