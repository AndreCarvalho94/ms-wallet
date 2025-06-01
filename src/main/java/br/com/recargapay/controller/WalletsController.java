package br.com.recargapay.controller;

import br.com.recargapay.controller.dto.BalanceResponse;
import br.com.recargapay.controller.dto.WalletRequest;
import br.com.recargapay.controller.mapper.BalanceMapper;
import br.com.recargapay.controller.mapper.WalletMapper;
import br.com.recargapay.entity.Balance;
import br.com.recargapay.usecase.CreateWallet;
import br.com.recargapay.usecase.ReadBalance;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/wallets")
public class WalletsController {

    private final CreateWallet createWallet;
    private final ReadBalance readBalance;
    private final BalanceMapper balanceMapper;

    @PostMapping
    public ResponseEntity<Void> createWallet(@RequestBody @Validated WalletRequest walletRequest) {
        createWallet.execute(walletRequest.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{walletId}/balance")
    public ResponseEntity<BalanceResponse> readBalance(@PathVariable UUID walletId){
        Balance balance = readBalance.execute(walletId);
        return ResponseEntity.ok(balanceMapper.toResponse(balance));
    }
}
