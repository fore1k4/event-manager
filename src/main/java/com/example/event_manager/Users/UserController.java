package com.example.event_manager.Users;

import com.example.event_manager.Locations.LocationDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserConverterDto userConverterDto;

    public UserController(UserService userService, UserConverterDto userConverterDto) {
        this.userService = userService;
        this.userConverterDto = userConverterDto;
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(
            @RequestBody UserDto user
    ) {
       var createdUser = userService.createUser(userConverterDto.toDomain(user));
       return ResponseEntity.status(HttpStatus.CREATED)
               .body(userConverterDto.toDto(createdUser));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        var users = userService.getAllUsers().stream()
                .map(userConverterDto::toDto)
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateLocation(
            @PathVariable("id") Integer id,
            @RequestBody UserDto userToUpdate
    ) {
        var updatedUser =
                userService.updateUser(id,userConverterDto.toDomain(userToUpdate));

        return ResponseEntity.status(HttpStatus.OK)
                .body(userConverterDto.toDto(updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(
            @PathVariable("id") Integer id
    ) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getLocationById(
            @PathVariable("id") Integer id
    ){
        var user = userService.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(userConverterDto.toDto(user));
    }
}
