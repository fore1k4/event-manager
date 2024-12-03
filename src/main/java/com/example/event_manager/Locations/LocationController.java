package com.example.event_manager.Locations;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;
    private final LocationConverterDto locationConverterDto;

    public LocationController(
            LocationService locationService,
            LocationConverterDto locationConverterDto
    ) {
        this.locationService = locationService;
        this.locationConverterDto = locationConverterDto;
    }

    @PostMapping
    public ResponseEntity<LocationDto> createLocation(
            @RequestBody LocationDto locationDto
    ) {
        var createdLocation = locationService.createLocation(locationConverterDto.toDomain(locationDto));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(locationConverterDto.toDto(createdLocation));
    }

    @GetMapping
    public ResponseEntity<List<LocationDto>> getAllLocations() {
        var locations = locationService.getAllLocations().stream()
                .map(locationConverterDto::toDto)
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(locations);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocationDto> updateLocation(
            @PathVariable("id") Integer id,
            @RequestBody LocationDto locationToUpdate
    ) {
       var updatedLocation =
               locationService.updateLocation(id,locationConverterDto.toDomain(locationToUpdate));

       return ResponseEntity.status(HttpStatus.OK)
               .body(locationConverterDto.toDto(updatedLocation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(
            @PathVariable("id") Integer id
    ) {
        locationService.deleteLocation(id);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationDto> getLocationById(
            @PathVariable("id") Integer id
    ){
        var location = locationService.getLocationById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(locationConverterDto.toDto(location));
    }





}
