package com.example.event_manager.events.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventRegistrationRepository extends JpaRepository<EventRegistrationEntity, Long> {

    boolean existsUserByEventId(Long id);

    @Query("""
             SELECT e FROM EventRegistrationEntity e
             where e.userId=:userId AND e.event.id=:eventId
            """)
    Optional<EventRegistrationEntity> findRegistration(
            @Param("userId") Long userId,
            @Param("eventId") Long eventId
    );

    @Query("""
            SELECT e.event.id FROM EventRegistrationEntity e
            WHERE  e.userId = :userId
            """)
    Optional<List<Long>> findEventsByUserId(
            @Param("userId") Long userId
    );


}
