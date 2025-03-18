package com.example.event_manager.locations.database;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationsRepository extends JpaRepository<LocationEntity, Long> {
    boolean existsByAddress(String address);

    void delete(LocationEntity locationEntity);

}
