package br.com.recargapay.usecase;

import br.com.recargapay.events.WithdrawFundsEvent;
import br.com.recargapay.exceptions.InvalidTransactionAmountException;
import br.com.recargapay.exceptions.WalletNotFoundException;
import br.com.recargapay.model.Balance;
import br.com.recargapay.model.Transaction;
import br.com.recargapay.model.TransactionType;
import br.com.recargapay.model.Wallet;
import br.com.recargapay.repository.BalanceRepository;
import br.com.recargapay.repository.TransactionRepository;
import br.com.recargapay.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WithdrawFunds {

    private final WalletRepository walletRepository;
    private final BalanceRepository balanceRepository;
    private final TransactionRepository transactionRepository;
    private final RetryTemplate retryTemplate;
    private final TransactionTemplate transactionTemplate;
    private final ApplicationEventPublisher eventPublisher;

    public Balance execute(UUID walletId, BigDecimal amount) {
        return retryTemplate.execute(context ->
                transactionTemplate.execute(status -> {
                    if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
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
                    transaction.setSourceResultingBalance(balance.getAmount());

                    transactionRepository.save(transaction);
                    eventPublisher.publishEvent(new WithdrawFundsEvent(transaction.getId(), walletId, amount, transaction.getCreatedAt()));
                    return balanceRepository.save(balance);
                })
        );
    }
}

