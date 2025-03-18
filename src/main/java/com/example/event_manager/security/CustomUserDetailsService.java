package com.example.event_manager.security;

import com.example.event_manager.users.database.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    private static Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Override
    public UserDetails loadUserByUsername(
            String username
    ) throws UsernameNotFoundException {
        logger.info("loadUserByUsername");

        var userEntity = userRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        return User.withUsername(username)
                .password(userEntity.getPassword())
                .authorities(userEntity.getRole())
                .build();


    }
}
