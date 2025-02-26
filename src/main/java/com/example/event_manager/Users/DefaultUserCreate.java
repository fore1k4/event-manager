package com.example.event_manager.Users;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DefaultUserCreate {
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @PostConstruct
    public void createTestUsers() {
        var hashedPassAdmin = passwordEncoder.encode("admin");
        var admin = new UserEntity(
                null,
                "admin",
                hashedPassAdmin,
                "ADMIN"
        );
        userRepository.save(admin);

        var hashedPassUser = passwordEncoder.encode("user");

        var user = new UserEntity(
                null,
                "user",
                hashedPassUser,
                "USER"
        );

        userRepository.save(user);
    }
}
