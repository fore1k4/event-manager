package com.example.event_manager.Events;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRepository extends JpaRepository<EventEntity, Integer> {
    boolean existsByName(String name);

    void delete(EventEntity eventToDelete);

}
