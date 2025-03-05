package com.example.event_manager.users;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public User registerUser(
            SignUpRequest signUpRequest
    ) {
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
        var userEntity = userRepository.findByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException("user not found"));
        return new User(
                userEntity.getId(),
                userEntity.getLogin(),
                userEntity.getPassword(),
                UserRole.valueOf(userEntity.getRole())
        );
    }

    public User getById(Long id) {
        var userEntity = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("user not found"));

        return new User(
                userEntity.getId(),
                userEntity.getLogin(),
                userEntity.getPassword(),
                UserRole.valueOf(userEntity.getRole())
        );
    }
}
