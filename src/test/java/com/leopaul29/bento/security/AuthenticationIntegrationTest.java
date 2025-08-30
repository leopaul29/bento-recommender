package com.leopaul29.bento.security;

import com.leopaul29.bento.config.TestDataBuilder;
import com.leopaul29.bento.entities.Role;
import com.leopaul29.bento.entities.User;
import com.leopaul29.bento.repositories.UserRepository;
import com.leopaul29.bento.services.impl.JwtService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
@Rollback
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthenticationIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestDataBuilder testDataBuilder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @LocalServerPort
    private int port;

    private String baseUrl;
    private User testUser;
    private User adminUser;
    private User disabledUser;
    private User lockedUser;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/auth";

        // Créer des utilisateurs de test
        testUser = testDataBuilder.createAndSaveTestUser("testuser", Role.USER);
        adminUser = testDataBuilder.createAndSaveTestUser("admin", Role.ADMIN);
        disabledUser = testDataBuilder.createAndSaveDisabledUser("disabled");
//        lockedUser = testDataBuilder.createLockedUser("locked", "locked@example.com");
    }

    @Test
    @Order(1)
    @DisplayName("Should successfully register a new user")
    void testUserRegistration_Success() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("password123")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RegisterRequest> entity = new HttpEntity<>(request, headers);

        // When
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
                baseUrl + "/register", entity, ApiResponse.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo("User registered successfully");

        // Vérifier que l'utilisateur existe en DB
        Optional<User> savedUser = userRepository.findByUsername("newuser");
        assertThat(savedUser).isPresent();
        savedUser.get().setDislikedIngredients(null);
        savedUser.get().setLikedTags(null);
//        assertThat(savedUser.get().getEmail()).isEqualTo("newuser@example.com");
        assertThat(savedUser.get().isEnabled()).isTrue();
    }

    @Test
    @Order(2)
    @DisplayName("Should fail registration with duplicate username")
    void testUserRegistration_DuplicateUsername() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .username("testuser") // Username already exists
                .email("different@example.com")
                .password("password123")
                .build();

        HttpEntity<RegisterRequest> entity = new HttpEntity<>(request);

        // When
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
                baseUrl + "/register", entity, ApiResponse.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("Username is already taken");
    }

    @Test
    @Order(3)
    @DisplayName("Should successfully login with valid credentials")
    void testLogin_Success() {
        // Given
        LoginRequest request = LoginRequest.builder()
                .username("testuser")
                .password("password123")
                .build();

        HttpEntity<LoginRequest> entity = new HttpEntity<>(request);

        // When
        ResponseEntity<JwtResponse> response = restTemplate.postForEntity(
                baseUrl + "/login", entity, JwtResponse.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        JwtResponse jwtResponse = response.getBody();
        assertThat(jwtResponse.getToken()).isNotEmpty();
        assertThat(jwtResponse.getUsername()).isEqualTo("testuser");
        assertThat(jwtResponse.getEmail()).isEqualTo("test@example.com");
        assertThat(jwtResponse.getRole()).isEqualTo("USER");
        assertThat(jwtResponse.getType()).isEqualTo("Bearer");
        assertThat(jwtResponse.getExpiresIn()).isGreaterThan(0);

        // Vérifier que le token JWT est valide
        String username = jwtService.extractUsername(jwtResponse.getToken());
        assertThat(username).isEqualTo("testuser");
    }

    @Test
    @Order(4)
    @DisplayName("Should fail login with invalid credentials")
    void testLogin_InvalidCredentials() {
        // Given
        LoginRequest request = LoginRequest.builder()
                .username("testuser")
                .password("wrongpassword")
                .build();

        HttpEntity<LoginRequest> entity = new HttpEntity<>(request);

        // When
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
                baseUrl + "/login", entity, ApiResponse.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid username or password");
    }

    @Test
    @Order(5)
    @DisplayName("Should fail login with disabled account")
    void testLogin_DisabledAccount() {
        // Given
        LoginRequest request = LoginRequest.builder()
                .username("disabled")
                .password("password123")
                .build();

        HttpEntity<LoginRequest> entity = new HttpEntity<>(request);

        // When
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
                baseUrl + "/login", entity, ApiResponse.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("Account is disabled");
    }

    @Test
    @Order(6)
    @DisplayName("Should fail login with locked account")
    void testLogin_LockedAccount() {
        // Given
        LoginRequest request = LoginRequest.builder()
                .username("locked")
                .password("password123")
                .build();

        HttpEntity<LoginRequest> entity = new HttpEntity<>(request);

        // When
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
                baseUrl + "/login", entity, ApiResponse.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("Account is locked");
    }

    @Test
    @Order(7)
    @DisplayName("Should get current user info with valid JWT")
    void testGetCurrentUser_Success() {
        // Given - Login first to get JWT
        String token = loginAndGetToken("testuser", "password123");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/me", HttpMethod.GET, entity, Map.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        Map<String, Object> userInfo = response.getBody();
        assertThat(userInfo.get("username")).isEqualTo("testuser");
        assertThat(userInfo.get("email")).isEqualTo("test@example.com");
        assertThat(userInfo.get("role")).isEqualTo("USER");
        assertThat(userInfo.get("enabled")).isEqualTo(true);
    }

    @Test
    @Order(8)
    @DisplayName("Should fail to get current user without JWT")
    void testGetCurrentUser_NoToken() {
        // Given - No authorization header
        HttpEntity<Void> entity = new HttpEntity<>(new HttpHeaders());

        // When
        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/me", HttpMethod.GET, entity, Map.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(401);
        assertThat(response.getBody().get("error")).isEqualTo("Unauthorized");
    }

    @Test
    @Order(9)
    @DisplayName("Should refresh JWT token successfully")
    void testRefreshToken_Success() {
        // Given - Login first to get JWT
        String token = loginAndGetToken("testuser", "password123");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<JwtResponse> response = restTemplate.exchange(
                baseUrl + "/refresh", HttpMethod.POST, entity, JwtResponse.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        JwtResponse jwtResponse = response.getBody();
        assertThat(jwtResponse.getToken()).isNotEmpty();
        assertThat(jwtResponse.getToken()).isNotEqualTo(token); // Nouveau token
        assertThat(jwtResponse.getUsername()).isEqualTo("testuser");
    }

    @Test
    @Order(10)
    @DisplayName("Should validate request body constraints")
    void testRegistration_ValidationErrors() {
        // Given - Invalid request data
        RegisterRequest request = RegisterRequest.builder()
                .username("u") // Too short
                .email("invalid-email") // Invalid format
                .password("123") // Too short
                .build();

        HttpEntity<RegisterRequest> entity = new HttpEntity<>(request);

        // When
        ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/register", entity, Map.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        // Les erreurs de validation sont gérées par Spring
    }

    // Helper method pour login et récupération du token
    private String loginAndGetToken(String username, String password) {
        LoginRequest loginRequest = LoginRequest.builder()
                .username(username)
                .password(password)
                .build();

        ResponseEntity<JwtResponse> loginResponse = restTemplate.postForEntity(
                baseUrl + "/login", new HttpEntity<>(loginRequest), JwtResponse.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).isNotNull();

        return loginResponse.getBody().getToken();
    }
}