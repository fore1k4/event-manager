package com.example.event_manager.Users;

public record User(
        Long id,
        String login,
        String password,
        UserRole role
) {

}
