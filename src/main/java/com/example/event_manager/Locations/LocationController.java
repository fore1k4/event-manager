package com.example.event_manager.Locations;

import com.example.event_manager.Events.EventController;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;
    private final LocationConverterDto locationConverterDto;
    private static final Logger logger = LoggerFactory.getLogger(LocationController.class);

    public LocationController(
            LocationService locationService,
            LocationConverterDto locationConverterDto
    ) {
        this.locationService = locationService;
        this.locationConverterDto = locationConverterDto;
    }

    @PostMapping
    public ResponseEntity<LocationDto> createLocation(
            @RequestBody @Valid LocationDto locationDto
    ) {
        logger.info("RECEIVED POST REQUEST for locationId = {}", locationDto.id());
        var createdLocation = locationService.createLocation(locationConverterDto.toDomain(locationDto));
        return ResponseEntity.status(201)
                .body(locationConverterDto.toDto(createdLocation));
    }

    @GetMapping
    public ResponseEntity<List<LocationDto>> getAllLocations() {
        logger.info("RECEIVED GET REQUEST");
        var locations = locationService.getAllLocations().stream()
                .map(locationConverterDto::toDto)
                .toList();

        return ResponseEntity.status(200)
                .body(locations);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocationDto> updateLocation(
            @PathVariable("id") Integer id,
            @RequestBody  @Valid LocationDto locationToUpdate
    ) {
        logger.info("RECEIVED PUT REQUEST for locationId = {}",id);
       var updatedLocation =
               locationService.updateLocation(id,locationConverterDto.toDomain(locationToUpdate));

       return ResponseEntity.status(200)
               .body(locationConverterDto.toDto(updatedLocation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(
            @PathVariable("id") Integer id
    ) {
        logger.info("RECEIVED DELETE REQUEST for id = {}",id);
        locationService.deleteLocation(id);
        return ResponseEntity.status(204)
                .build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationDto> getLocationById(
            @PathVariable("id") Integer id
    ){
        logger.info("RECEIVED GET REQUEST for locationId = {}", id);
        var location = locationService.getLocationById(id);
        return ResponseEntity.status(200)
                .body(locationConverterDto.toDto(location));
    }


}
