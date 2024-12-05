package com.example.event_manager.Users;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserService {
    private final UserRepository userRepository;

    private final UserConverterEntity userConverterEntity;

    public UserService(UserRepository userRepository, UserConverterEntity userConverterEntity) {
        this.userRepository = userRepository;
        this.userConverterEntity = userConverterEntity;
    }

    public User createUser(
            User user
    ) {
        if(userRepository.existsByLogin(user.login())) {
            throw new EntityExistsException("user with login = %s, already exists"
                    .formatted(user.login()));
        }
        var createdUser = userRepository.save(userConverterEntity.toEntity(user));

        return userConverterEntity.toDomain(createdUser);
    }

    public List<User> getAllUsers() {
      return  userRepository.findAll().stream()
              .map(userConverterEntity::toDomain)
              .toList();
    }

    public User getUserById(
            Integer id
    ) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("user with id = %s, not found"
                    .formatted(id));
        }
        return userConverterEntity.toDomain(userRepository.findById(id).orElseThrow());
    }
    public void deleteUser(
            Integer id
    ) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("user with id = %s, not found"
                    .formatted(id));
        }
        var userToDelete = userRepository.findById(id).orElseThrow();
        userRepository.delete(userToDelete);
    }

    public User updateUser(
            Integer id,
            User userToUpdate
    ) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("user with id = %s, not found"
                    .formatted(id));
        }

        if(userRepository.existsByLogin(userToUpdate.login())) {
            throw new EntityExistsException("user with login = %s, already exists"
                    .formatted(userToUpdate.login()));
        }

        var updatedUser = new UserEntity(
                userToUpdate.id(),
                userToUpdate.login(),
                userToUpdate.age(),
                userToUpdate.role()
        );
        updatedUser.setId(id);

        userRepository.save(updatedUser);

        return userConverterEntity.toDomain(updatedUser);
    }
}
