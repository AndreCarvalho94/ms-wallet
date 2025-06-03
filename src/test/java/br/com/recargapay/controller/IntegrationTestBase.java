package br.com.recargapay.controller;

import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.CockroachContainer;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntegrationTestBase {

    protected static final UUID DEFAULT_WALLET_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    protected static final UUID DEFAULT_WALLET_ID_2 = UUID.fromString("21111111-1111-1111-1111-111111111111");
    protected static final UUID DEFAULT_WALLET_ID_3 = UUID.fromString("31111111-1111-1111-1111-111111111111");
    protected static final UUID DEFAULT_WALLET_ID_4 = UUID.fromString("42222222-2222-2222-2222-222222222222");
    protected static final UUID DEFAULT_WALLET_ID_5 = UUID.fromString("51111111-1111-1111-1111-111111111111");


    @LocalServerPort
    private Integer port;

    static CockroachContainer cockroach = new CockroachContainer("cockroachdb/cockroach:v24.1.0")
            .withCommand("start-single-node --insecure")
            .withExposedPorts(26257);


    @BeforeEach
    void setUpEach() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", cockroach::getJdbcUrl);
        registry.add("spring.datasource.username", cockroach::getUsername);
        registry.add("spring.datasource.password", cockroach::getPassword);
        registry.add("spring.flyway.enabled", () -> "false");
        registry.add("spring.flyway.baseline-version", () -> "1.0");
        registry.add("spring.flyway.baseline-on-migrate", () -> "true");
        registry.add("spring.sql.init.data-locations", () -> "classpath:/db/database_creation.sql");
        registry.add("spring.sql.init.mode", () -> "always");
    }


    @BeforeAll
    static void setUp() {
        cockroach.start();
    }

    @AfterAll
    static void tearDown() {
        cockroach.stop();
    }

}
