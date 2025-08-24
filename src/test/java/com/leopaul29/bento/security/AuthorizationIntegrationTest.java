package com.leopaul29.bento.security;

import com.leopaul29.bento.config.TestDataBuilder;
import com.leopaul29.bento.entities.Role;
import com.leopaul29.bento.entities.User;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
@Rollback
class AuthorizationIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestDataBuilder testDataBuilder;

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
                baseUrl + "/api/bento", HttpMethod.GET, entity, String.class);

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
                baseUrl + "/api/bento", HttpMethod.GET, entity, Map.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(401);
        assertThat(response.getBody().get("error")).isEqualTo("Unauthorized");
        assertThat(response.getBody().get("message")).isEqualTo("JWT token is missing or invalid");
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
                baseUrl + "/api/bento", HttpMethod.GET, entity, Map.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Should allow ADMIN access to admin endpoints")
    void testAdminEndpoint_AdminUser_Success() {
        // Given
        String adminToken = loginAndGetToken("admin", "admin123");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/api/admin/accounts/statistics", HttpMethod.GET, entity, Map.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsKeys("total", "enabled", "disabled", "locked");
    }

    @Test
    @DisplayName("Should deny USER access to admin endpoints")
    void testAdminEndpoint_RegularUser_Denied() {
        // Given
        String userToken = loginAndGetToken("user", "password123");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/api/admin/accounts/statistics", HttpMethod.GET, entity, Map.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(403);
        assertThat(response.getBody().get("error")).isEqualTo("Forbidden");
        assertThat(response.getBody().get("message")).isEqualTo("Access denied");
    }

    @Test
    @DisplayName("Should allow ADMIN to disable user accounts")
    void testDisableAccount_AdminUser_Success() {
        // Given
        Long targetUserId = regularUser.getId();
        String adminToken = loginAndGetToken("admin", "admin123");

        DisableAccountRequest request = new DisableAccountRequest();
        request.setReason("Test suspension");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<DisableAccountRequest> entity = new HttpEntity<>(request, headers);

        // When
        ResponseEntity<ApiResponse> response = restTemplate.exchange(
                baseUrl + "/api/admin/accounts/" + targetUserId + "/disable",
                HttpMethod.POST, entity, ApiResponse.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo("Account disabled successfully");
    }

    @Test
    @DisplayName("Should deny USER from disabling accounts")
    void testDisableAccount_RegularUser_Denied() {
        // Given
        Long targetUserId = regularUser.getId();
        String userToken = loginAndGetToken("user", "password123");

        DisableAccountRequest request = new DisableAccountRequest();
        request.setReason("Attempting unauthorized action");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<DisableAccountRequest> entity = new HttpEntity<>(request, headers);

        // When
        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/api/admin/accounts/" + targetUserId + "/disable",
                HttpMethod.POST, entity, Map.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("Should handle role-based authorization correctly for different endpoints")
    void testMultipleEndpoints_DifferentRoles() {
        // Given
        String userToken = loginAndGetToken("user", "password123");
        String adminToken = loginAndGetToken("admin", "admin123");

        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(userToken);
        HttpEntity<Void> userEntity = new HttpEntity<>(userHeaders);

        HttpHeaders adminHeaders = new HttpHeaders();
        adminHeaders.setBearerAuth(adminToken);
        HttpEntity<Void> adminEntity = new HttpEntity<>(adminHeaders);

        // When & Then - Test multiple endpoints

        // 1. Public endpoint - accessible to all
        ResponseEntity<String> publicResponse = restTemplate.getForEntity(
                baseUrl + "/api/auth/login", String.class);
        assertThat(publicResponse.getStatusCode()).isNotEqualTo(HttpStatus.FORBIDDEN);

        // 2. User endpoint - accessible to authenticated users
        ResponseEntity<String> userBentoResponse = restTemplate.exchange(
                baseUrl + "/api/bento", HttpMethod.GET, userEntity, String.class);
        assertThat(userBentoResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<String> adminBentoResponse = restTemplate.exchange(
                baseUrl + "/api/bento", HttpMethod.GET, adminEntity, String.class);
        assertThat(adminBentoResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 3. Admin endpoint - only accessible to admins
        ResponseEntity<Map> userAdminResponse = restTemplate.exchange(
                baseUrl + "/api/admin/accounts/statistics", HttpMethod.GET, userEntity, Map.class);
        assertThat(userAdminResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        ResponseEntity<Map> adminAdminResponse = restTemplate.exchange(
                baseUrl + "/api/admin/accounts/statistics", HttpMethod.GET, adminEntity, Map.class);
        assertThat(adminAdminResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
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
                baseUrl + "/api/bento", HttpMethod.GET, entity, Map.class);

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
                baseUrl + "/api/bento", HttpMethod.GET, entity, Map.class);

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

// DTO pour les tests
@Data
class DisableAccountRequest {
    private String reason;
}