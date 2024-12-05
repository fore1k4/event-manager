package com.example.event_manager.Users;

import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    boolean existsByLogin(String login);
    void delete(UserEntity userToDelete);
}
