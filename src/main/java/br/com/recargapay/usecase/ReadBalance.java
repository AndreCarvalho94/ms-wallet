package br.com.recargapay.usecase;

import br.com.recargapay.entity.Balance;
import br.com.recargapay.exceptions.WalletNotFoundException;
import br.com.recargapay.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReadBalance {

    private final BalanceRepository repository;

    public Balance execute(UUID walletId) {
        Optional<Balance> wallet = repository.findByWalletId(walletId);
        return wallet.orElseThrow(() -> new WalletNotFoundException(walletId));
    }
}
