package com.example.event_manager.locations.api;

import com.example.event_manager.locations.domain.LocationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;
    private final LocationDtoConverter locationDtoConverter;
    private static final Logger logger = LoggerFactory.getLogger(LocationController.class);

    public LocationController(
            LocationService locationService,
            LocationDtoConverter locationDtoConverter
    ) {
        this.locationService = locationService;
        this.locationDtoConverter = locationDtoConverter;
    }

    @PostMapping
    public ResponseEntity<LocationDto> createLocation(
            @RequestBody @Valid LocationDto locationDto
    ) {
        logger.info("RECEIVED POST REQUEST for locationId = {}", locationDto.id());
        var createdLocation = locationService.createLocation(locationDtoConverter.toDomain(locationDto));
        return ResponseEntity.status(201)
                .body(locationDtoConverter.toDto(createdLocation));
    }

    @GetMapping
    public ResponseEntity<List<LocationDto>> getAllLocations() {
        logger.info("RECEIVED GET REQUEST");
        var locations = locationService.getAllLocations().stream()
                .map(locationDtoConverter::toDto)
                .toList();

        return ResponseEntity.status(200)
                .body(locations);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocationDto> updateLocation(
            @PathVariable("id") Long id,
            @RequestBody @Valid LocationDto locationToUpdate
    ) {
        logger.info("RECEIVED PUT REQUEST for locationId = {}", id);
        var updatedLocation =
                locationService.updateLocation(id, locationDtoConverter.toDomain(locationToUpdate));

        return ResponseEntity.status(200)
                .body(locationDtoConverter.toDto(updatedLocation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(
            @PathVariable("id") Long id
    ) {
        logger.info("RECEIVED DELETE REQUEST for id = {}", id);
        locationService.deleteLocation(id);
        return ResponseEntity.status(204)
                .build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationDto> getLocationById(
            @PathVariable("id") Long id
    ) {
        logger.info("RECEIVED GET REQUEST for locationId = {}", id);
        var location = locationService.getLocationById(id);
        return ResponseEntity.status(200)
                .body(locationDtoConverter.toDto(location));
    }


}
