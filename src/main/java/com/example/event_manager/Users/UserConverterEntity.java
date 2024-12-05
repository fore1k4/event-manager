package com.example.event_manager.Users;

import org.springframework.stereotype.Component;

@Component
public class UserConverterEntity {
    public User toDomain(UserEntity user) {
        return new User(
                user.getId(),
                user.getLogin(),
                user.getAge(),
                user.getRole()
        );
    }
    public UserEntity toEntity(User user) {
        return new UserEntity(
                user.id(),
                user.login(),
                user.age(),
                user.role()
        );
    }
}
