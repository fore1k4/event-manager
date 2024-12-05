package com.example.event_manager.Locations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LocationsRepository extends JpaRepository<LocationEntity, Integer> {
    boolean existsByAddress(String address);

    void delete(LocationEntity locationEntity);
    @Query("""
            SELECT l
            FROM LocationEntity l
            """)
    List<LocationEntity> getAllLocations();
}
