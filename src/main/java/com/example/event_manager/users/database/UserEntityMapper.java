package com.example.event_manager.users.database;

import com.example.event_manager.users.domain.User;
import org.springframework.stereotype.Component;

@Component
public class UserEntityMapper {

    public UserEntity toEntity(User user) {
        return new UserEntity(
                user.id(),
                user.login(),
                user.password(),
                user.role().toString()
        );
    }
}
