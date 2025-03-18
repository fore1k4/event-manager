package com.example.event_manager.users.api;

import com.example.event_manager.security.jwt.AuthenticationService;
import com.example.event_manager.users.domain.UserService;
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

    private final AuthenticationService authenticationService;

    public UserController(
            UserService userService,
            AuthenticationService authenticationService
    ) {
        this.userService = userService;
        this.authenticationService = authenticationService;
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
        var token = authenticationService.authenticate(signInRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new JwtTokenResponse(token));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserInfo(
            @RequestBody @PathVariable Long id
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
