package com.leopaul29.bento.security;

import com.leopaul29.bento.config.TestDataBuilder;
import com.leopaul29.bento.entities.Role;
import com.leopaul29.bento.entities.User;
import com.leopaul29.bento.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthorizationIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestDataBuilder testDataBuilder;

    @Autowired
    private UserRepository userRepository;

    @LocalServerPort
    private int port;

    private String baseUrl;
    private String authUrl;
    private User regularUser;
    private User adminUser;
    private User moderatorUser;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
        authUrl = baseUrl + "/api/auth";

        // Créer des utilisateurs avec différents rôles
        regularUser = testDataBuilder.createAndSaveTestUser("user", Role.USER);
        adminUser = testDataBuilder.createAndSaveTestUser("admin", Role.ADMIN);
        moderatorUser = testDataBuilder.createAndSaveTestUser("mod", Role.MODERATOR);
    }

    @AfterEach
    void tearDown() {
        // The fixtures are committed, not rolled back, so each test cleans up after itself.
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should allow authenticated users to access protected bento endpoints")
    void testProtectedEndpoint_AuthenticatedUser_Success() {
        // Given
        String userToken = loginAndGetToken("user", "password123");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/api/bentos", HttpMethod.GET, entity, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Should deny access to protected endpoints without authentication")
    void testProtectedEndpoint_NoAuth_Denied() {
        // Given - No authentication
        HttpEntity<Void> entity = new HttpEntity<>(new HttpHeaders());

        // When
        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/api/bentos", HttpMethod.GET, entity, Map.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(401);
        assertThat(response.getBody().get("error")).isEqualTo("Unauthorized");
        assertThat(response.getBody().get("message")).isEqualTo("Authentication required");
    }

    @Test
    @DisplayName("Should deny access with invalid JWT token")
    void testProtectedEndpoint_InvalidToken_Denied() {
        // Given - Invalid token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("invalid.jwt.token");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/api/bentos", HttpMethod.GET, entity, Map.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Should handle expired JWT tokens")
    void testExpiredToken_Access_Denied() {
        // Given - Créer un token expiré (nécessite une modification temporaire du JwtService)
        // Pour ce test, on simule avec un token malformé
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("eyJhbGciOiJIUzI1NiJ9.expired.token");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/api/bentos", HttpMethod.GET, entity, Map.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Should allow access to user profile endpoint for authenticated users")
    void testUserProfile_AuthenticatedAccess() {
        // Given
        String userToken = loginAndGetToken("user", "password123");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<Map> response = restTemplate.exchange(
                authUrl + "/me", HttpMethod.GET, entity, Map.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("username")).isEqualTo("user");
        assertThat(response.getBody().get("role")).isEqualTo("USER");
    }

    @Test
    @DisplayName("Should validate JWT token signature")
    void testInvalidSignature_Access_Denied() {
        // Given - Token avec signature invalide
        String invalidToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiZXhwIjoxNjA5NDU5MjAwfQ.invalid_signature";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(invalidToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/api/bentos", HttpMethod.GET, entity, Map.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    // Helper method
    private String loginAndGetToken(String username, String password) {
        LoginRequest loginRequest = LoginRequest.builder()
                .username(username)
                .password(password)
                .build();

        ResponseEntity<JwtResponse> loginResponse = restTemplate.postForEntity(
                authUrl + "/login", new HttpEntity<>(loginRequest), JwtResponse.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).isNotNull();

        return loginResponse.getBody().getToken();
    }
}
