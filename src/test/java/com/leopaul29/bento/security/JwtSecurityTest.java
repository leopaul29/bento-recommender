package com.leopaul29.bento.security;

import com.leopaul29.bento.config.TestDataBuilder;
import com.leopaul29.bento.entities.Role;
import com.leopaul29.bento.entities.User;
import com.leopaul29.bento.services.impl.JwtService;
import com.leopaul29.bento.services.impl.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class JwtSecurityTest {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private TestDataBuilder testDataBuilder;

    private UserDetails testUserDetails;

    @BeforeEach
    void setUp() {
        User testUser = testDataBuilder.createAndSaveTestUser("testuser", Role.USER);
        testUserDetails = new UserPrincipal(testUser);
    }

    @Test
    @DisplayName("Should generate valid JWT token")
    void testGenerateToken_Valid() {
        // When
        String token = jwtService.generateToken(testUserDetails);

        // Then
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts: header.payload.signature

        String extractedUsername = jwtService.extractUsername(token);
        assertThat(extractedUsername).isEqualTo("testuser");

        boolean isValid = jwtService.isTokenValid(token, testUserDetails);
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should extract correct claims from JWT token")
    void testExtractClaims_Success() {
        // Given
        Map<String, Object> extraClaims = Map.of("customClaim", "customValue");
        String token = jwtService.generateToken(extraClaims, testUserDetails);

        // When
        String username = jwtService.extractUsername(token);
        Date expiration = jwtService.extractClaim(token, Claims::getExpiration);
        Date issuedAt = jwtService.extractClaim(token, Claims::getIssuedAt);

        // Then
        assertThat(username).isEqualTo("testuser");
        assertThat(expiration).isAfter(new Date());
        assertThat(issuedAt).isBefore(new Date());
        assertThat(expiration.getTime() - issuedAt.getTime()).isEqualTo(jwtService.getExpirationTime());
    }

    @Test
    @DisplayName("Should validate token correctly")
    void testTokenValidation_Success() {
        // Given
        String token = jwtService.generateToken(testUserDetails);

        // When & Then
        assertThat(jwtService.isTokenValid(token, testUserDetails)).isTrue();
    }

    @Test
    @DisplayName("Should reject token for different user")
    void testTokenValidation_WrongUser() {
        // Given
        String token = jwtService.generateToken(testUserDetails);
        User differentUser = testDataBuilder.createAndSaveTestUser("different", Role.USER);
        UserDetails differentUserDetails = new UserPrincipal(differentUser);

        // When & Then
        assertThat(jwtService.isTokenValid(token, differentUserDetails)).isFalse();
    }

    @Test
    @DisplayName("Should handle malformed JWT token")
    void testMalformedToken_ThrowsException() {
        // Given
        String malformedToken = "not.a.valid.jwt";

        // When & Then
        assertThatThrownBy(() -> jwtService.extractUsername(malformedToken))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should handle empty token")
    void testEmptyToken_ThrowsException() {
        // Given
        String emptyToken = "";

        // When & Then
        assertThatThrownBy(() -> jwtService.extractUsername(emptyToken))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should include authorities in token")
    void testTokenWithAuthorities() {
        // Given
        User adminUser = testDataBuilder.createAndSaveTestUser("admin", Role.ADMIN);
        UserDetails adminUserDetails = new UserPrincipal(adminUser);

        // When
        String token = jwtService.generateToken(adminUserDetails);

        // Then
        String username = jwtService.extractUsername(token);
        assertThat(username).isEqualTo("admin");

        // Vérifier que les authorities sont incluses dans le token
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKeyForTest())
                .build()
                .parseClaimsJws(token)
                .getBody();

        @SuppressWarnings("unchecked")
        List<String> authorities = (List<String>) claims.get("authorities");
        assertThat(authorities).contains("ROLE_ADMIN");
    }

    @Test
    @DisplayName("Should have correct token expiration")
    void testTokenExpiration() {
        // Given
        String token = jwtService.generateToken(testUserDetails);

        // When
        Date expiration = jwtService.extractClaim(token, Claims::getExpiration);
        Date issuedAt = jwtService.extractClaim(token, Claims::getIssuedAt);

        // Then
        long expectedDuration = jwtService.getExpirationTime();
        long actualDuration = expiration.getTime() - issuedAt.getTime();

        // Allow 1-second tolerance for test execution time
        assertThat(actualDuration).isBetween(expectedDuration - 1000, expectedDuration + 1000);
    }
    @Value("${jwt.secret}")
    String secretKey;

    // Helper method pour obtenir la clé de signature (pour les tests seulement)
    private Key getSigningKeyForTest() {
        byte[] keyBytes = Decoders.BASE64.decode(Base64.getEncoder().encodeToString(secretKey.getBytes()));
        return Keys.hmacShaKeyFor(keyBytes);
    }
}