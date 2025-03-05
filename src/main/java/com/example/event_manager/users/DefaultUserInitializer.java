package com.example.event_manager.users;

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
        var hashedPassAdmin = passwordEncoder.encode("admin");
        var admin = new UserEntity(
                null,
                "admin",
                hashedPassAdmin,
                "ADMIN"
        );

        if (userRepository.existsByLogin("admin")) {
            throw new EntityExistsException("User already exists");
        }
        userRepository.save(admin);

        var hashedPassUser = passwordEncoder.encode("user");

        var user = new UserEntity(
                null,
                "user",
                hashedPassUser,
                "USER"
        );
        if (userRepository.existsByLogin("user")) {
            throw new EntityExistsException("User already exists");
        }
        userRepository.save(user);

    }
}
