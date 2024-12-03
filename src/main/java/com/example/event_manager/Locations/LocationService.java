package com.example.event_manager.Locations;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {
    private final LocationConverterEntity locationConverterEntity;
    private final LocationsRepository locationsRepository;

    public LocationService(
            LocationConverterEntity locationConverterEntity,
            LocationsRepository locationsRepository
    ) {
        this.locationConverterEntity = locationConverterEntity;
        this.locationsRepository = locationsRepository;
    }


    public Location createLocation(
            Location location
    ) {
      if(locationsRepository.existsByAddress(location.address())) {
          throw new EntityExistsException("location with address = %s, already exists"
                  .formatted(location.address()));
      }
      var newLocation = locationsRepository.save(locationConverterEntity.toEntity(location));

      return locationConverterEntity.toDomain(newLocation);
    }

    public List<Location> getAllLocations() {
        return locationsRepository.findAll().stream()
                .map(locationConverterEntity::toDomain)
                .toList();
    }
    public void deleteLocation(
            Integer locationId
    ) {
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
        if(!locationsRepository.existsById(id)) {
            throw new EntityNotFoundException("location with id = %s, not found"
                    .formatted(id));
        }
       return locationConverterEntity.toDomain(locationsRepository.findById(id).orElseThrow());
    }

    public Location updateLocation(
            Integer id,
            Location locationToUpdate
    ) {
        if(!locationsRepository.existsByAddress(locationToUpdate.address())) {
            throw new EntityExistsException("location with address = %s, already exists"
                    .formatted(locationToUpdate.address()));
        }

        var updatedLocation = new LocationEntity(
                locationToUpdate.id(),
                locationToUpdate.address(),
                locationToUpdate.capacity(),
                locationToUpdate.description()
        );
        updatedLocation.setId(id);
        locationsRepository.save(updatedLocation);

        return locationConverterEntity.toDomain(updatedLocation);
    }
}
