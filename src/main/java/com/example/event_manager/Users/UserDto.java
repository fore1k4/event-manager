package com.example.event_manager.Users;

public record UserDto (
        Integer id,
        String login,
        Integer age,
        String role
) {
}
