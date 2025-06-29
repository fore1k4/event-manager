package com.example.event_manager.security.jwt;

import com.example.event_manager.users.api.SignInRequest;
import com.example.event_manager.users.domain.User;
import com.example.event_manager.users.domain.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenManager jwtTokenManager;
    @Autowired
    private UserService userService;


    public String authenticate(
            @Valid SignInRequest signInRequest
    ) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInRequest.login(),
                        signInRequest.password()
                )
        );
        var userId = userService.findByLogin(signInRequest.login()).id();

        return jwtTokenManager.createJwtToken(signInRequest.login(), userId);
    }

    public User getCurrentAuthenticatedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("Authentication not present");
        }
        return (User) authentication.getPrincipal();
    }

    public String getCurrentUserJwtToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new IllegalStateException("Authentication not present");
        }

        return (String) authentication.getCredentials();
    }
}
