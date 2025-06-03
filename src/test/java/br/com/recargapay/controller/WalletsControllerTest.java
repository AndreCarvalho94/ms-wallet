package br.com.recargapay.controller;

import br.com.recargapay.controller.dto.DepositFundsRequest;
import br.com.recargapay.controller.dto.TransferFundsRequest;
import br.com.recargapay.controller.dto.WalletRequest;
import br.com.recargapay.controller.dto.WithdrawFundsRequest;
import br.com.recargapay.model.Wallet;
import br.com.recargapay.repository.BalanceRepository;
import br.com.recargapay.usecase.CreateWallet;
import br.com.recargapay.usecase.DepositFunds;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

class WalletsControllerTest extends IntegrationTestBase {

    @Autowired
    private BalanceRepository repository;

    @Autowired
    private CreateWallet createWallet;

    @Autowired
    private DepositFunds depositFunds;

    private static final int THREAD_COUNT = 10;
    private static final BigDecimal DEPOSIT_AMOUNT = BigDecimal.TEN;
    private static final BigDecimal WITHDRAW_AMOUNT = BigDecimal.TEN;

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

    @Test
    void shouldHandleConcurrentDepositsSafely() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        DepositFundsRequest depositFundsRequest = new DepositFundsRequest(DEPOSIT_AMOUNT);
        Wallet wallet = createWallet.execute(UUID.randomUUID());
        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.submit(() -> {
                try {
                    given()
                            .contentType("application/json")
                            .pathParam("walletId", wallet.getId())
                            .body(depositFundsRequest)
                            .when()
                            .post("/api/v1/wallets/{walletId}/deposit")
                            .then()
                            .statusCode(HttpStatus.OK.value());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        given()
                .pathParam("walletId", wallet.getId())
                .when()
                .get("/api/v1/wallets/{walletId}/balance")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("amount", equalTo(THREAD_COUNT * DEPOSIT_AMOUNT.floatValue()));
    }

    @Test
    @Sql("/db/withdraw_funds_test.sql")
    void shouldWithdrawFundsSuccessfully() {
        BigDecimal amount = BigDecimal.valueOf(50.00);
        WithdrawFundsRequest depositFundsRequest = new WithdrawFundsRequest(amount);

        given()
                .pathParam("walletId", DEFAULT_WALLET_ID_6)
                .contentType(ContentType.JSON)
                .body(depositFundsRequest)
                .when()
                .post("/api/v1/wallets/{walletId}/withdraw")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("amount", equalTo(BigDecimal.ZERO.floatValue()));
    }

    @Test
    void shouldReturnUnprocessableEntityWhenWithdrawingNegativeAmount() {
        Wallet wallet = createWallet.execute(UUID.randomUUID());
        BigDecimal negativeAmount = BigDecimal.valueOf(-10.00);
        WithdrawFundsRequest withdrawFundsRequest = new WithdrawFundsRequest(negativeAmount);

        given()
                .pathParam("walletId", wallet.getId())
                .contentType(ContentType.JSON)
                .body(withdrawFundsRequest)
                .when()
                .post("/api/v1/wallets/{walletId}/withdraw")
                .then()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .body("message", equalTo("Invalid transaction amount. Amount must be greater than zero."));
    }

    @Test
    void shouldReturnUnprocessableEntityWhenWithdrawingInvalidAmount() {
        Wallet wallet = createWallet.execute(UUID.randomUUID());
        BigDecimal invalidAmount = BigDecimal.TEN;
        WithdrawFundsRequest withdrawFundsRequest = new WithdrawFundsRequest(invalidAmount);

        given()
                .pathParam("walletId", wallet.getId())
                .contentType(ContentType.JSON)
                .body(withdrawFundsRequest)
                .when()
                .post("/api/v1/wallets/{walletId}/withdraw")
                .then()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .body("message", equalTo("Insufficient funds for withdrawal."));
    }

    @Test
    void shouldReturnNotFoundWhenWithdrawingFromNonExistentWallet() {
        UUID nonExistentWalletId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(50.00);
        WithdrawFundsRequest withdrawFundsRequest = new WithdrawFundsRequest(amount);

        given()
                .pathParam("walletId", nonExistentWalletId)
                .contentType(ContentType.JSON)
                .body(withdrawFundsRequest)
                .when()
                .post("/api/v1/wallets/{walletId}/withdraw")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", equalTo("Wallet not found with ID: " + nonExistentWalletId));
    }

    @Test
    void shouldHandleConcurrentWithdrawalsSafely() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        WithdrawFundsRequest withdrawRequest = new WithdrawFundsRequest(WITHDRAW_AMOUNT);

        Wallet wallet = createWallet.execute(UUID.randomUUID());
        depositFunds.execute(wallet.getId(), BigDecimal.valueOf(THREAD_COUNT).multiply(WITHDRAW_AMOUNT));

        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.submit(() -> {
                try {
                    given()
                            .contentType("application/json")
                            .pathParam("walletId", wallet.getId())
                            .body(withdrawRequest)
                            .when()
                            .post("/api/v1/wallets/{walletId}/withdraw")
                            .then()
                            .statusCode(HttpStatus.OK.value());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        given()
                .pathParam("walletId", wallet.getId())
                .when()
                .get("/api/v1/wallets/{walletId}/balance")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("amount", equalTo(0f));
    }

    @Test
    void shouldHandleConcurrentTransfersBetweenTwoWalletsSafely() throws InterruptedException {

        Wallet walletA = createWallet.execute(UUID.randomUUID());
        Wallet walletB = createWallet.execute(UUID.randomUUID());

        depositFunds.execute(walletA.getId(), DEPOSIT_AMOUNT.multiply(BigDecimal.valueOf(THREAD_COUNT)));
        depositFunds.execute(walletB.getId(), DEPOSIT_AMOUNT.multiply(BigDecimal.valueOf(THREAD_COUNT)));

        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {
            UUID source = (i % 2 == 0) ? walletA.getId() : walletB.getId();
            UUID destination = (i % 2 == 0) ? walletB.getId() : walletA.getId();

            executor.submit(() -> {
                try {
                    TransferFundsRequest request = new TransferFundsRequest(source, destination, DEPOSIT_AMOUNT);
                    given()
                            .contentType("application/json")
                            .body(request)
                            .when()
                            .post("/api/v1/wallets/transfer")
                            .then()
                            .statusCode(HttpStatus.OK.value());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        BigDecimal expectedFinalBalance = DEPOSIT_AMOUNT.multiply(BigDecimal.valueOf(THREAD_COUNT));

        given()
                .pathParam("walletId", walletA.getId())
                .when()
                .get("/api/v1/wallets/{walletId}/balance")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("amount", equalTo(expectedFinalBalance.floatValue()));

        given()
                .pathParam("walletId", walletB.getId())
                .when()
                .get("/api/v1/wallets/{walletId}/balance")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("amount", equalTo(expectedFinalBalance.floatValue()));
    }

    @Test
    void shouldReturnUnprocessableEntityWhenInsufficientFunds() {
        Wallet sourceWallet = createWallet.execute(UUID.randomUUID());
        Wallet destinationWallet = createWallet.execute(UUID.randomUUID());

        depositFunds.execute(sourceWallet.getId(), BigDecimal.valueOf(50.00));

        BigDecimal transferAmount = BigDecimal.valueOf(100.00);
        TransferFundsRequest transferFundsRequest = new TransferFundsRequest(
                sourceWallet.getId(),
                destinationWallet.getId(),
                transferAmount
        );

        given()
                .contentType(ContentType.JSON)
                .body(transferFundsRequest)
                .when()
                .post("/api/v1/wallets/transfer")
                .then()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .body("message", equalTo("Insufficient funds in source wallet."));
    }


}