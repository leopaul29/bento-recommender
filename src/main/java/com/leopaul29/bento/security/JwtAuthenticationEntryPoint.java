package com.leopaul29.bento.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// JWT Authentication Entry Point: 401 error code
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    Logger log = LogManager.getLogger(JwtAuthenticationEntryPoint.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // Log pour debugging
        log.warn("Unauthorized access attempt to: {} from IP: {}",
                request.getRequestURI(),
                request.getRemoteAddr());

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 error code

        // Message adapted regarding the error type
        String message = determineErrorMessage(authException);

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", message);
        body.put("path", request.getServletPath());
        body.put("timestamp", new Date());

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

    private String determineErrorMessage(AuthenticationException ex) {
        if (ex instanceof BadCredentialsException) {
            return "Invalid credentials provided";
        } else if (ex instanceof DisabledException) {
            return "Account is disabled";
        }
        return "Authentication required";
    }
}
