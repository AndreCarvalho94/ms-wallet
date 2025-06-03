package br.com.recargapay.controller;

import br.com.recargapay.controller.dto.DepositFundsRequest;
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
    @Sql("/db/read_balance_test.sql")
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

    @Test
    @Sql("/db/daily_balance_test.sql")
    void shouldReadDailyBalanceSuccessfully() {
        String date = "2025-06-02";

        given()
                .pathParam("walletId", DEFAULT_WALLET_ID_2)
                .queryParam("date", date)
                .when()
                .get("/api/v1/wallets/{walletId}/daily-balance")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("amount", equalTo(130.0f));
    }

    @Test
    @Sql("/db/daily_balance_bi_directional_test.sql")
    void shouldReturnCorrectDailyBalanceAfterBidirectionalTransfers() {
        String date = "2025-06-02";

        given()
                .pathParam("walletId", DEFAULT_WALLET_ID_3)
                .queryParam("date", date)
                .when()
                .get("/api/v1/wallets/{walletId}/daily-balance")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("amount", equalTo(450.0f));

        given()
                .pathParam("walletId", DEFAULT_WALLET_ID_4)
                .queryParam("date", date)
                .when()
                .get("/api/v1/wallets/{walletId}/daily-balance")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("amount", equalTo(350.0f));
    }

    @Test
    void shouldReturnWalletNotFoundWhenDailyBalanceWalletDoesNotExist() {
        UUID nonExistentWalletId = UUID.randomUUID();
        given()
                .pathParam("walletId", nonExistentWalletId)
                .when()
                .get("/api/v1/wallets/{walletId}/daily-balance")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", equalTo("Wallet not found with ID: " + nonExistentWalletId));
    }

    @Test
    @Sql("/db/deposit_funds_test.sql")
    void shouldDepositFundsMultipleTimesSuccessfully() {
        BigDecimal amount = BigDecimal.valueOf(100.00);
        DepositFundsRequest depositFundsRequest = new DepositFundsRequest(amount);

        given()
                .pathParam("walletId", DEFAULT_WALLET_ID_5)
                .contentType(ContentType.JSON)
                .body(depositFundsRequest)
                .when()
                .post("/api/v1/wallets/{walletId}/deposit")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("amount", equalTo(depositFundsRequest.getAmount().floatValue()));

        given()
                .pathParam("walletId", DEFAULT_WALLET_ID_5)
                .contentType(ContentType.JSON)
                .body(depositFundsRequest)
                .when()
                .post("/api/v1/wallets/{walletId}/deposit")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("amount", equalTo(amount.multiply(BigDecimal.TWO).floatValue()));
    }

    @Test
    void shouldReturnNotFoundWhenDepositingToNonExistentWallet() {
        UUID nonExistentWalletId = UUID.randomUUID();
        DepositFundsRequest depositFundsRequest = new DepositFundsRequest(BigDecimal.valueOf(100.00));

        given()
                .pathParam("walletId", nonExistentWalletId)
                .contentType(ContentType.JSON)
                .body(depositFundsRequest)
                .when()
                .post("/api/v1/wallets/{walletId}/deposit")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", equalTo("Wallet not found with ID: " + nonExistentWalletId));
    }

    @Test
    void shouldReturnUnprocessableEntityWhenDepositingWithInvalidAmount() {
        DepositFundsRequest depositFundsRequest = new DepositFundsRequest(BigDecimal.ZERO);

        given()
                .pathParam("walletId", DEFAULT_WALLET_ID)
                .contentType(ContentType.JSON)
                .body(depositFundsRequest)
                .when()
                .post("/api/v1/wallets/{walletId}/deposit")
                .then()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .body("message", equalTo("Invalid transaction amount. Amount must be greater than zero."));
    }
}