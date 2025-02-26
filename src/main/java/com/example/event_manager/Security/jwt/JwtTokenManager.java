package com.example.event_manager.Security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenManager {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SecretKey secretKey;

    private final Integer expiration;

    public JwtTokenManager(
            @Value("${jwt.secret-key}")String keyString,
            @Value("${jwt.lifetime}")Integer expiration
    ) {
        this.secretKey = Keys.hmacShaKeyFor(keyString.getBytes());
        this.expiration = expiration;
    }

    public String createJwtToken(String login ) {
        logger.info("Creating jwt token");
       return  Jwts.builder()
                 .setSubject(login)
                 .signWith(secretKey)
                 .setIssuedAt(new Date())
                 .setExpiration(new Date(System.currentTimeMillis() + expiration))
                 .compact();
    }

    public String getLoginFromJwtToken(String token) {
        logger.info("Getting login from token");
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

    }
}
