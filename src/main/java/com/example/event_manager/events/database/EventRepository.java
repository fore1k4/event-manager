package com.example.event_manager.events.database;

import com.example.event_manager.events.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<EventEntity, Long> {

    boolean existsByName(String name);

    Optional<EventEntity> findById(Long id);

    @Query("""
            SELECT e.places FROM EventEntity e
            WHERE e.id =:eventId
            """)
    Long getMaxPlaces(@Param("eventId") Long eventId);

    @Query("""
            SELECT e.occupiedPlaces FROM EventEntity e
            WHERE e.id =:eventId
            """)
    Long getOccupiedPlaces(@Param("eventId") Long eventId);

    @Transactional
    @Modifying
    @Query("""
            UPDATE EventEntity e SET e.occupiedPlaces = e.occupiedPlaces + 1 
            WHERE e.id = :eventId
            """)
    void addPersoneOnEventPlace(@Param("eventId") Long eventId);

    @Transactional
    @Modifying
    @Query("""
            UPDATE EventEntity e SET e.occupiedPlaces = e.occupiedPlaces - 1 
            WHERE e.id = :eventId
            """)
    void minusPersoneOnEventPlace(@Param("eventId") Long eventId);


    List<EventEntity> findEventsByOwnerId(Long ownerId);

    @Query("""
            SELECT e FROM EventEntity e
            WHERE (:name IS NULL OR e.name LIKE :name)
            AND (:ownerId IS NULL OR e.ownerId = :ownerId)
            AND (:maxPlaces IS NULL OR e.places <= :maxPlaces)
            AND (:minPlaces IS NULL OR e.places  >= :minPlaces)
            AND (:maxCost IS NULL OR e.cost <= :maxCost)
            AND (:minCost IS NULL OR e.cost >= :minCost)
            AND (:status IS NULL OR e.status = :status)
            AND (:locationId IS NULL OR e.locationId = :locationId)
            AND (:maxDuration IS NULL OR e.duration <= :maxDuration)
            AND (:minDuration IS NULL OR e.duration >= :minDuration)
            AND (CAST(:timeBefore as date) IS NULL OR e.date <= :timeBefore)
            AND (CAST(:timeAfter as date) IS NULL OR e.date >= :timeAfter)
            """)
    List<EventEntity> findEvents(
            @Param("name") String name,
            @Param("ownerId") Long ownerId,
            @Param("maxPlaces") Long maxPlaces,
            @Param("minPlaces") Long minPlaces,
            @Param("minCost") Long minCost,
            @Param("maxCost") Long maxCost,
            @Param("timeBefore") ZonedDateTime timeBefore,
            @Param("timeAfter") ZonedDateTime timeAfter,
            @Param("status") EventStatus status,
            @Param("minDuration") Long minDuration,
            @Param("maxDuration") Long maxDuration,
            @Param("locationId") Long locationId
    );

    @Modifying
    @Transactional
    @Query("""
            UPDATE EventEntity e SET e.status = :newStatus
            WHERE e.id = :eventId
            """)
    void updateEventStatus(
            @Param("eventId") Long eventId,
            @Param("newStatus") EventStatus newStatus
    );

    @Query("""
                SELECT e FROM EventEntity e
                WHERE e.id IN :eventId
            """)
    List<EventEntity> findAllByEventId(
            @Param("eventId") List<Long> eventId
    );


}

