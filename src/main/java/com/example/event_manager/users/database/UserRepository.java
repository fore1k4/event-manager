package com.example.event_manager.users.database;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<UserEntity, java.lang.Long> {
    boolean existsByLogin(String login);

    Optional<UserEntity> findByLogin(String login);


}
