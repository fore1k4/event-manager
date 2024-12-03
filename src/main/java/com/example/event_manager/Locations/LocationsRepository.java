package com.example.event_manager.Locations;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationsRepository extends JpaRepository<LocationEntity, Integer> {
    boolean existsByAddress(String address);

    void delete(LocationEntity locationEntity);
}
