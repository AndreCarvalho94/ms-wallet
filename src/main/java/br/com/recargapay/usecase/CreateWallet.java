package br.com.recargapay.usecase;

import br.com.recargapay.events.WalletCreation;
import br.com.recargapay.model.Balance;
import br.com.recargapay.model.Wallet;
import br.com.recargapay.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateWallet {

    private final WalletRepository repository;
    private final ApplicationEventPublisher eventPublisher;


    public Wallet execute(UUID userId){
        Wallet wallet = new Wallet();
        wallet.setUserId(userId);

        Balance balance = new Balance();
        balance.setWallet(wallet);
        wallet.setBalance(balance);
        Wallet savedWallet = repository.save(wallet);
        eventPublisher.publishEvent(new WalletCreation(wallet.getId(), userId, balance.getAmount(), savedWallet.getBalance().getCreatedAt()));
        return savedWallet;
    }
}
