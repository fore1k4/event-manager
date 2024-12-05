package com.example.event_manager.Locations;

import com.example.event_manager.Events.EventConverterEntity;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LocationService {
    private final LocationConverterEntity locationConverterEntity;
    private final LocationsRepository locationsRepository;
    private final EventConverterEntity eventConverterEntity;
    private static final Logger logger = LoggerFactory.getLogger(LocationService.class);

    public LocationService(
            LocationConverterEntity locationConverterEntity,
            LocationsRepository locationsRepository,
            EventConverterEntity eventConverterEntity
    ) {
        this.locationConverterEntity = locationConverterEntity;
        this.locationsRepository = locationsRepository;
        this.eventConverterEntity = eventConverterEntity;
    }

    @Transactional
    public Location createLocation(
            Location location
    ) {
        logger.info("location create process witn locationId = {}", location.id());

        if(locationsRepository.existsByAddress(location.address())) {
          throw new EntityExistsException("location with address = %s, already exists"
                  .formatted(location.address()));
        }

        var newLocation = locationsRepository.save(locationConverterEntity.toEntity(location));

        return locationConverterEntity.toDomain(newLocation);
    }

    public List<Location> getAllLocations() {
        logger.info("get all location  process");

        return locationsRepository.findAll().stream()
                .map(locationConverterEntity::toDomain)
                .toList();
    }
    @Transactional
    public void deleteLocation(
            Integer locationId
    ) {
        logger.info("location delete process with locationId = {}", locationId);

        var location = getLocationById(locationId);

        if(!locationsRepository.existsByAddress(location.address())) {
            throw new EntityNotFoundException("location with address = %s, not found"
                    .formatted(location.address()));
        }

         locationsRepository.delete(locationConverterEntity.toEntity(location));
    }
    public Location getLocationById(
            Integer id
    ) {
        logger.info("get location process witn locationId = {}", id);

        if(!locationsRepository.existsById(id)) {
            throw new EntityNotFoundException("location with id = %s, not found"
                    .formatted(id));
        }

       return locationConverterEntity.toDomain(locationsRepository.findById(id).orElseThrow());
    }
    @Transactional
    public Location updateLocation(
            Integer id,
            Location locationToUpdate
    ) {
        logger.info("update location process with locationId = {}", id);

        if(!locationsRepository.existsById(id)) {
            throw new EntityNotFoundException("location with id = %s, not found"
                    .formatted(id));
        }


        var updatedLocation = new LocationEntity(
                locationToUpdate.id(),
                locationToUpdate.address(),
                locationToUpdate.capacity(),
                locationToUpdate.description(),
                locationToUpdate.events().stream()
                        .map(event -> eventConverterEntity.toEntity(event))
                        .toList()
        );

        if(locationsRepository.existsByAddress(updatedLocation.getAddress())) {
            throw new EntityExistsException("location with address = %s, already exists"
                    .formatted(locationToUpdate.address()));
        }

        updatedLocation.setId(id);
        locationsRepository.save(updatedLocation);

        return locationConverterEntity.toDomain(updatedLocation);
    }
    public LocationEntity getLocationByIdEntity(Integer id) {
        return locationsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Location with id = %s not found"
                        .formatted(id)));
    }


}
