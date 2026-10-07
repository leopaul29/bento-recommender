package com.leopaul29.bento.ordering.infrastructure;

import com.leopaul29.bento.entities.Bento;
import com.leopaul29.bento.ordering.domain.BentoId;
import com.leopaul29.bento.ordering.domain.ShopDay;
import com.leopaul29.bento.ordering.domain.ports.ShopDayRepository;
import com.leopaul29.bento.repositories.BentoRepository;
import com.leopaul29.bento.security.LoginRequest;
import com.leopaul29.bento.security.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The whole stack, once: register, log in, place an order over HTTP, read it back out of a real
 * PostgreSQL, cancel it, and see the cancellation persisted.
 *
 * <p>Postgres, not H2, deliberately. The ordering tables use an {@code @ElementCollection} list
 * with an {@code @OrderColumn} and a map with a {@code @MapKeyColumn} — mappings that can work on
 * one dialect and fail on another — and this repo has already been bitten once by H2 standing in
 * for Postgres.
 *
 * <p><strong>No Docker means no run — except in CI, where it means failure.</strong> Skipping is a
 * convenience on a laptop without Docker and a false green in the one environment with nobody
 * watching. {@link #dockerAvailableOrRunningInCi} therefore enables this class whenever {@code CI}
 * is set, regardless of Docker, so a CI runner without a daemon errors on container startup
 * instead of reporting a test it never executed.
 *
 * <p>{@code @Testcontainers(disabledWithoutDocker = true)} cannot express that: its condition is
 * evaluated before {@code @BeforeAll}, so a guard written there never runs in exactly the case it
 * exists to catch. That was the first attempt, and it was dead code.
 */
@Testcontainers
@EnabledIf("dockerAvailableOrRunningInCi")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderingEndToEndIT {

    @Container
    @SuppressWarnings("resource") // Testcontainers manages the lifecycle
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:14-alpine");

    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.sql.init.mode", () -> "never");
        registry.add("jwt.secret",
                () -> "endToEndSecretKeyLongEnoughForHs256Signing1234567890123456");
        registry.add("jwt.expiration", () -> "3600000");
    }

    /**
     * True in CI always, and locally only when Docker is there.
     *
     * <p>Returning true without Docker is deliberate: the container then fails to start and the
     * build goes red, which is the correct outcome for a CI run that cannot exercise the database.
     */
    @SuppressWarnings("unused") // referenced by @EnabledIf
    static boolean dockerAvailableOrRunningInCi() {
        return System.getenv("CI") != null || DockerClientFactory.instance().isDockerAvailable();
    }

    private static final LocalDate SERVICE_DATE = LocalDate.now().plusDays(1);

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private BentoRepository bentos;

    @Autowired
    private ShopDayRepository shopDays;

    @LocalServerPort
    private int port;

    private String baseUrl;
    private long karaageId;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;

        Bento karaage = bentos.save(Bento.builder()
                .name("Karaage bento").description("fried chicken").calorie(700).priceYen(620)
                .ingredients(Set.of()).tags(Set.of())
                .build());
        karaageId = karaage.getId();

        // Ordering closes an hour from now, so the cutoff rule is satisfied without freezing time.
        shopDays.save(ShopDay.of(
                SERVICE_DATE,
                Instant.now().plus(1, ChronoUnit.HOURS),
                Map.of(BentoId.of(karaageId), 5)));
    }

    @Test
    @DisplayName("register, log in, order, read it back from Postgres, cancel")
    void theWholeOrderingPathOverHttp() {
        String token = registerAndLogin("e2e-customer", "password123");

        // Place.
        ResponseEntity<Map> placed = rest.exchange(
                baseUrl + "/api/orders", HttpMethod.POST,
                json(token, Map.of(
                        "serviceDate", SERVICE_DATE.toString(),
                        "items", java.util.List.of(Map.of("bentoId", karaageId, "quantity", 2)))),
                Map.class);

        assertThat(placed.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(placed.getBody()).isNotNull();
        assertThat(placed.getBody().get("status")).isEqualTo("PLACED");
        assertThat(((Number) placed.getBody().get("totalYen")).longValue()).isEqualTo(2 * 620L);

        String orderId = (String) placed.getBody().get("id");
        assertThat(orderId).isNotBlank();

        // Read it back — out of Postgres, through the adapter, into the aggregate, out as JSON.
        ResponseEntity<Map> fetched = rest.exchange(
                baseUrl + "/api/orders/" + orderId, HttpMethod.GET, auth(token), Map.class);

        assertThat(fetched.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(fetched.getBody()).isNotNull();
        assertThat(fetched.getBody().get("status")).isEqualTo("PLACED");
        assertThat(((Number) fetched.getBody().get("totalYen")).longValue()).isEqualTo(1240L);

        // Cancel, and see the new status persisted rather than held in memory.
        ResponseEntity<Map> cancelled = rest.exchange(
                baseUrl + "/api/orders/" + orderId + "/cancel", HttpMethod.POST,
                auth(token), Map.class);
        assertThat(cancelled.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<Map> afterCancel = rest.exchange(
                baseUrl + "/api/orders/" + orderId, HttpMethod.GET, auth(token), Map.class);
        assertThat(afterCancel.getBody()).isNotNull();
        assertThat(afterCancel.getBody().get("status")).isEqualTo("CANCELLED");
    }

    @Test
    @DisplayName("a bento that is not on the day's menu is a 400, not a 500")
    void anOffMenuBentoIsABadRequest() {
        String token = registerAndLogin("e2e-offmenu", "password123");

        Bento offMenu = bentos.save(Bento.builder()
                .name("Tomorrow only").description("not today").calorie(400).priceYen(500)
                .ingredients(Set.of()).tags(Set.of())
                .build());

        ResponseEntity<Map> response = rest.exchange(
                baseUrl + "/api/orders", HttpMethod.POST,
                json(token, Map.of(
                        "serviceDate", SERVICE_DATE.toString(),
                        "items", java.util.List.of(
                                Map.of("bentoId", offMenu.getId(), "quantity", 1)))),
                Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(String.valueOf(response.getBody().get("message"))).contains("not on the menu");
    }

    @Test
    @DisplayName("ordering without a token is refused")
    void orderingWithoutATokenIsRefused() {
        ResponseEntity<Map> response = rest.exchange(
                baseUrl + "/api/orders", HttpMethod.POST,
                json(null, Map.of("serviceDate", SERVICE_DATE.toString(),
                        "items", java.util.List.of(Map.of("bentoId", karaageId, "quantity", 1)))),
                Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private String registerAndLogin(String username, String password) {
        rest.postForEntity(baseUrl + "/api/auth/register",
                new HttpEntity<>(RegisterRequest.builder()
                        .username(username).email(username + "@example.com").password(password)
                        .build()),
                Map.class);

        ResponseEntity<Map> login = rest.postForEntity(baseUrl + "/api/auth/login",
                new HttpEntity<>(LoginRequest.builder().username(username).password(password).build()),
                Map.class);

        assertThat(login.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(login.getBody()).isNotNull();
        return (String) login.getBody().get("token");
    }

    private HttpEntity<Object> json(String token, Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (token != null) {
            headers.setBearerAuth(token);
        }
        return new HttpEntity<>(body, headers);
    }

    private HttpEntity<Void> auth(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return new HttpEntity<>(headers);
    }
}
