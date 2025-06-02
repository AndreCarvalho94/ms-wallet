package br.com.recargapay.controller;

import br.com.recargapay.controller.dto.WalletRequest;
import br.com.recargapay.repository.BalanceRepository;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

class WalletsControllerTest extends IntegrationTestBase {

    @Autowired
    private BalanceRepository repository;

    @Test
    void shouldCreateWalletSuccessfully() {
        WalletRequest walletRequest = new WalletRequest();
        walletRequest.setUserId(UUID.randomUUID());

        given()
            .contentType(ContentType.JSON)
            .body(walletRequest)
        .when()
            .post("/api/v1/wallets")
        .then()
            .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    @Sql("/db/insert_wallet_and_balance.sql")
    void shouldReadBalanceSuccessfully() {
        BigDecimal amount = repository.findByWalletId(DEFAULT_WALLET_ID).get().getAmount();
        given()
            .pathParam("walletId", DEFAULT_WALLET_ID)
        .when()
            .get("/api/v1/wallets/{walletId}/balance")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("amount", equalTo(amount.floatValue()))
            .body("updatedAt", notNullValue());
    }

    @Test
    void shouldReturnNotFoundWhenWalletDoesNotExist() {
        UUID nonExistentWalletId = UUID.randomUUID();
        given()
            .pathParam("walletId", nonExistentWalletId)
        .when()
            .get("/api/v1/wallets/{walletId}/balance")
        .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("message", equalTo("Wallet not found with ID: " + nonExistentWalletId));
    }

    @Test
    void shouldReturnBadRequestWhenWalletIdIsInvalid() {
        given()
                .pathParam("walletId", "INVALID_UUID")
                .when()
                .get("/api/v1/wallets/{walletId}/balance")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo("The provided parameter is invalid: INVALID_UUID"));
    }
}