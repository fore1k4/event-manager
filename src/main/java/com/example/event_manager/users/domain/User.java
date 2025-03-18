package com.example.event_manager.users.domain;

import com.example.event_manager.users.UserRole;

public record User(
        Long id,
        String login,
        String password,
        UserRole role
) {

}
