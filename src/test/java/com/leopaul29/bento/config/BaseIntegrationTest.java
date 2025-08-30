package com.leopaul29.bento.config;

import com.leopaul29.bento.entities.Role;
import com.leopaul29.bento.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public abstract class BaseIntegrationTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected TestDataBuilder testDataBuilder;

    // URLs de base
    protected String baseUrl;
    protected String authUrl;

    // Utilisateurs de test communs
    protected User testUser;
    protected User regularUser;
    protected User adminUser;
    protected User moderatorUser;
    protected User disabledUser;
    protected User lockedUser;

    @BeforeEach
    void setUp() {
        // Construction des URLs
        baseUrl = "http://localhost:" + port;
        authUrl = baseUrl + "/auth";

        // Création des utilisateurs de test
        testUser = testDataBuilder.createAndSaveTestUser("testUser",  Role.USER);
        regularUser = testDataBuilder.createAndSaveTestUser("user",  Role.USER);
        adminUser = testDataBuilder.createAndSaveTestUser("admin",  Role.ADMIN);
        moderatorUser = testDataBuilder.createAndSaveTestUser("moderator",  Role.MODERATOR);
        disabledUser = testDataBuilder.createAndSaveDisabledUser("disabled");
//        lockedUser = testDataBuilder.createAndSaveLockedUser("locked", "locked123");
    }
}