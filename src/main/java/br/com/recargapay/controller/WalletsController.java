package br.com.recargapay.controller;

import br.com.recargapay.controller.dto.BalanceResponse;
import br.com.recargapay.controller.dto.DepositFundsRequest;
import br.com.recargapay.controller.dto.WalletRequest;
import br.com.recargapay.controller.dto.WithdrawFundsRequest;
import br.com.recargapay.controller.mapper.BalanceMapper;
import br.com.recargapay.model.Balance;
import br.com.recargapay.model.DailyBalance;
import br.com.recargapay.usecase.CreateWallet;
import br.com.recargapay.usecase.DepositFunds;
import br.com.recargapay.usecase.ReadBalance;
import br.com.recargapay.usecase.WithdrawFunds;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/wallets")
public class WalletsController {

    private final CreateWallet createWallet;
    private final ReadBalance readBalance;
    private final BalanceMapper balanceMapper;
    private final DepositFunds depositFunds;
    private final WithdrawFunds withdrawFunds;

    @PostMapping
    public ResponseEntity<Void> createWallet(@RequestBody @Validated WalletRequest walletRequest) {
        createWallet.execute(walletRequest.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{walletId}/balance")
    public ResponseEntity<BalanceResponse> readBalance(@PathVariable UUID walletId) {
        Balance balance = readBalance.execute(walletId);
        return ResponseEntity.ok(balanceMapper.toResponse(balance));
    }

    @GetMapping("/{walletId}/daily-balance")
    public ResponseEntity<DailyBalance> readDailyBalance(@PathVariable UUID walletId,
                                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(readBalance.execute(walletId, date));
    }

    @PostMapping("/{walletId}/deposit")
    public ResponseEntity<BalanceResponse> depositFunds(@PathVariable UUID walletId, @RequestBody DepositFundsRequest depositFundsRequest) {
        Balance balance = depositFunds.execute(walletId, depositFundsRequest.getAmount());
        return ResponseEntity.ok().body(balanceMapper.toResponse(balance));
    }

    @PostMapping("/{walletId}/withdraw")
    public ResponseEntity<BalanceResponse> withdrawFunds(@PathVariable UUID walletId, @RequestBody WithdrawFundsRequest withdrawFundsRequest) {
        Balance balance = withdrawFunds.execute(walletId, withdrawFundsRequest.getAmount());
        return ResponseEntity.ok().body(balanceMapper.toResponse(balance));
    }
}
