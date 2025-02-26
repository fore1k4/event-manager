package com.example.event_manager.Users;

import org.springframework.stereotype.Component;

@Component
public class UserConverterDto {
    public User toDomain(UserDto userDto) {
        return new User(
                userDto.id(),
                userDto.login(),
                userDto.age(),
                userDto.role()
        );
    }
    public UserDto toDto (User user) {
        return new UserDto(
                user.id(),
                user.login(),
                user.age(),
                user.role()
        );
    }
}
