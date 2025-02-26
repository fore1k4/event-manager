package com.example.event_manager.Users;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserService {
    private static Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public User registerUser(
            SignUpRequest signUpRequest
    ) {
        log.info("Registering user");
        if (userRepository.existsByLogin(signUpRequest.login())) {
            throw new EntityExistsException("User already exists");
        }

        var hashedPass = passwordEncoder.encode(signUpRequest.password());

        var user = new UserEntity(
                null,
                signUpRequest.login(),
                hashedPass,
                UserRole.USER.toString()
        );

        userRepository.save(user);

        return new User(
                user.getId(),
                user.getLogin(),
                user.getPassword(),
                UserRole.valueOf(user.getRole())
        );
    }

    public User findByLogin(String login) {
        log.info("Finding user by login");
        var userEntity = userRepository.findByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException("user not found"));
        return new User(
                userEntity.getId(),
                userEntity.getLogin(),
                userEntity.getPassword(),
                UserRole.valueOf(userEntity.getRole())
        );
    }
}
