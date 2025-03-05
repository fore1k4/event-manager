package com.example.event_manager.users;

import com.example.event_manager.security.jwt.JwtAuthenticationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    ) {
        logger.info("Controller Creating user");
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
        logger.info("Controller Authenticating user");
        var token = jwtAuthenticationService.authenticate(signInRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new JwtTokenResponse(token));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserInfo(
            @RequestBody Long id
    ) {
        logger.info("Controller getting user info");

        var user = userService.getById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new UserDto(
                        user.id(),
                        user.login()
                ));
    }
}
