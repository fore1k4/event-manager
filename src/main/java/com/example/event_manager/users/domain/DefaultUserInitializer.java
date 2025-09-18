package com.example.event_manager.users.domain;

import com.example.event_manager.users.database.UserEntity;
import com.example.event_manager.users.database.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DefaultUserInitializer {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initializeUsers() {
        if (!userRepository.existsByLogin("admin")) {
            var admin = new UserEntity(
                    null,
                    "admin",
                    passwordEncoder.encode("admin"),
                    "ADMIN"
            );
            userRepository.save(admin);
        } else {
            System.out.println("Admin already exists, skipping creation.");
        }

        if (!userRepository.existsByLogin("user")) {
            var user = new UserEntity(
                    null,
                    "user",
                    passwordEncoder.encode("user"),
                    "USER"
            );
            userRepository.save(user);
        } else {
            System.out.println("User already exists, skipping creation.");
        }
    }
}
