package com.example.event_manager.Security;

import com.example.event_manager.GlobalExceptionHandler.ServerErrorDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAuthenticateExceptionHandler implements AuthenticationEntryPoint {

    private static Logger logger = LoggerFactory.getLogger(CustomAuthenticateExceptionHandler.class);
    @Autowired
    private ObjectMapper objectMapper;
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        logger.info("CustomAuthenticateExceptionHandler");

        var responseMessage = new ServerErrorDto(
                "Got authentication exception.",
                authException.getMessage(),
                LocalDateTime.now()
        );

        var responseString = objectMapper.writeValueAsString(responseMessage);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(responseString);
    }
}
