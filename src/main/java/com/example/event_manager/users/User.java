package com.example.event_manager.users;

public record User(
        Long id,
        String login,
        String password,
        UserRole role
) {

}
