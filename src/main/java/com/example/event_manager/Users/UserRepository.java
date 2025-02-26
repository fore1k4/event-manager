package com.example.event_manager.Users;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    boolean existsByLogin(String login);

    Optional<UserEntity> findByLogin(String login);
}
