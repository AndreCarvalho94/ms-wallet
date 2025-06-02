package br.com.recargapay.usecase;

import br.com.recargapay.entity.Balance;
import br.com.recargapay.entity.Wallet;
import br.com.recargapay.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateWallet {

    private final WalletRepository repository;

    public void execute(UUID userId){
        Wallet wallet = new Wallet();
        wallet.setUserId(userId);

        Balance balance = new Balance();
        balance.setWallet(wallet);
        wallet.setBalance(balance);
        repository.save(wallet);
    }
}
