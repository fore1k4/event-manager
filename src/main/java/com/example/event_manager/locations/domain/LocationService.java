package com.example.event_manager.locations.domain;

import com.example.event_manager.events.database.EventRepository;
import com.example.event_manager.events.domain.EventService;
import com.example.event_manager.locations.database.LocationsRepository;
import com.example.event_manager.locations.database.LocationEntity;
import com.example.event_manager.locations.database.LocationEntityConverter;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final EventRepository eventRepository;
    private final LocationEntityConverter locationEntityConverter;
    private final LocationsRepository locationsRepository;
    private static final Logger logger = LoggerFactory.getLogger(LocationService.class);


    @Transactional
    public Location createLocation(
            Location location
    ) {
        logger.info("location create process witn locationId = {}", location.id());

        if (locationsRepository.existsByAddress(location.address())) {
            throw new EntityExistsException("location with address = %s, already exists"
                    .formatted(location.address()));
        }
        var newLocation = locationsRepository.save(locationEntityConverter.toEntity(location));

        return locationEntityConverter.toDomain(newLocation);
    }

    public List<Location> getAllLocations() {
        logger.info("get all location  process");

        return locationsRepository.findAll().stream()
                .map(locationEntityConverter::toDomain)
                .toList();
    }

    @Transactional
    public void deleteLocation(
            Long locationId
    ) {
        logger.info("location delete process with locationId = {}", locationId);

        var location = getLocationById(locationId);

        if (!locationsRepository.existsByAddress(location.address())) {
            throw new EntityNotFoundException("location with address = %s, not found"
                    .formatted(location.address()));
        }

        locationsRepository.delete(locationEntityConverter.toEntity(location));
    }

    public Location getLocationById(
            Long id
    ) {
        logger.info("get location process witn locationId = {}", id);

        if (!locationsRepository.existsById(id)) {
            throw new EntityNotFoundException("location with id = %s, not found"
                    .formatted(id));
        }

        return locationEntityConverter.toDomain(locationsRepository.findById(id).orElseThrow());
    }

    @Transactional
    public Location updateLocation(
            Long id,
            Location newLocation
    ) {
        logger.info("update location process with locationId = {}", id);

        var location = getLocationById(id);

        if (!locationsRepository.existsById(id)) {
            throw new EntityNotFoundException("location with id = %s, not found"
                    .formatted(id));
        }

        var updatedLocationEntity = new LocationEntity(
                newLocation.id(),
                newLocation.name(),
                newLocation.address(),
                newLocation.capacity(),
                newLocation.description()
        );

        if (locationsRepository.existsByAddress(updatedLocationEntity.getAddress())) {
            throw new EntityExistsException("location with address = %s, already exists"
                    .formatted(updatedLocationEntity.getAddress()));
        }

        if (eventRepository.existsLocationById(id)) {
            if (newLocation.capacity() < location.capacity()) {
                throw new IllegalArgumentException("location capacity exceeds capacity");
            }
        }

        updatedLocationEntity.setId(id);
        locationsRepository.save(updatedLocationEntity);

        return locationEntityConverter.toDomain(updatedLocationEntity);
    }

}
