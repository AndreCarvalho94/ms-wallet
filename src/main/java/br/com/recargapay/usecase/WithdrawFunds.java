package br.com.recargapay.usecase;

import br.com.recargapay.exceptions.InvalidTransactionAmountException;
import br.com.recargapay.exceptions.WalletNotFoundException;
import br.com.recargapay.model.Balance;
import br.com.recargapay.model.Transaction;
import br.com.recargapay.model.TransactionType;
import br.com.recargapay.model.Wallet;
import br.com.recargapay.repository.BalanceRepository;
import br.com.recargapay.repository.TransactionRepository;
import br.com.recargapay.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WithdrawFunds {

    private final WalletRepository walletRepository;
    private final BalanceRepository balanceRepository;
    private final TransactionRepository transactionRepository;


    @Retryable(
            retryFor = TransientDataAccessException.class,
            backoff = @Backoff(delay = 200)
    )
    @Transactional
    public Balance execute(UUID walletId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionAmountException();
        }
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(walletId));

        Balance balance = wallet.getBalance();
        if (balance.getAmount().compareTo(amount) < 0) {
            throw new InvalidTransactionAmountException("Insufficient funds for withdrawal.");
        }
        balance.setAmount(balance.getAmount().subtract(amount));
        Transaction transaction = new Transaction();
        transaction.setSource(wallet);
        transaction.setAmount(amount);
        transaction.setType(TransactionType.WITHDRAW);
        transaction.setBalance(balance);
        transactionRepository.save(transaction);

        return balanceRepository.save(balance);
    }
}
