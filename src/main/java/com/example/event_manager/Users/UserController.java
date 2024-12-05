package com.example.event_manager.Users;

import com.example.event_manager.Locations.LocationDto;
import com.example.event_manager.Security.jwt.JwtAuthenticationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    private final JwtAuthenticationService jwtAuthenticationService;

    public UserController(
            UserService userService,
            JwtAuthenticationService jwtAuthenticationService
    ) {
        this.userService = userService;
        this.jwtAuthenticationService = jwtAuthenticationService;
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(
            @RequestBody @Valid SignUpRequest signUpRequest
    )
    {
        logger.info("Creating user");
        var createdUser = userService.registerUser(signUpRequest);

        return ResponseEntity.status(HttpStatus.OK).
                body(new UserDto(
                        createdUser.id(),
                        createdUser.login()
                ));
    }

    @PostMapping("/auth")
    public ResponseEntity<JwtTokenResponse> authenticateUser(
            @RequestBody @Valid SignInRequest signInRequest
    ) {
        logger.info("Authenticating user");
         var token = jwtAuthenticationService.authenticate(signInRequest);

         return ResponseEntity.status(HttpStatus.OK)
                 .body(new JwtTokenResponse(token));
    }
}
