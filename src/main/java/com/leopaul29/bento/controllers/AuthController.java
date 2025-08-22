package com.leopaul29.bento.controllers;

import com.leopaul29.bento.entities.Role;
import com.leopaul29.bento.entities.User;
import com.leopaul29.bento.repositories.UserRepository;
import com.leopaul29.bento.security.*;
import com.leopaul29.bento.services.impl.JwtService;
import com.leopaul29.bento.services.impl.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtService.generateToken(userDetails);

            // Récupérer les infos utilisateur
            UserPrincipal userPrincipal = (UserPrincipal) userDetails;
            User user = userPrincipal.getUser();

            JwtResponseDto response = JwtResponseDto.builder()
                    .token(jwt)
                    .username(user.getUsername())
//                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .expiresIn(jwtService.getExpirationTime())
                    .build();

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto(false, "Invalid username or password"));
        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto(false, "An error occurred during login"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDto registerRequest) {
        try {
            // Vérifier si l'utilisateur existe déjà
            if (userRepository.existsByUsername(registerRequest.getUsername())) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponseDto(false, "Username is already taken"));
            }

//            if (userRepository.existsByEmail(registerRequest.getEmail())) {
//                return ResponseEntity.badRequest()
//                        .body(new ApiResponseDto(false, "Email is already in use"));
//            }

            // Create new user
            User user = User.builder()
                    .username(registerRequest.getUsername())
//                    .email(registerRequest.getEmail())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .role(Role.USER)
                    .enabled(true)
                    .build();

            userRepository.save(user);

            return ResponseEntity.ok(new ApiResponseDto(true, "User registered successfully"));

        } catch (Exception e) {
            log.error("Registration error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto(false, "An error occurred during registration"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto(false, "No token provided"));
        }

        try {
            String jwt = authHeader.substring(7);
            String username = jwtService.extractUsername(jwt);

            if (username != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                String newToken = jwtService.generateToken(userDetails);

                UserPrincipal userPrincipal = (UserPrincipal) userDetails;
                User user = userPrincipal.getUser();

                JwtResponseDto response = JwtResponseDto.builder()
                        .token(newToken)
                        .username(user.getUsername())
//                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .expiresIn(jwtService.getExpirationTime())
                        .build();

                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            log.error("Token refresh error: {}", e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponseDto(false, "Invalid token"));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userPrincipal.getUser();

        Map<String, Object> userInfo = Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
//                "email", user.getEmail(),
                "role", user.getRole().name(),
                "enabled", user.isEnabled(),
                "createdAt", user.getCreatedAt()
        );

        return ResponseEntity.ok(userInfo);
    }
}
