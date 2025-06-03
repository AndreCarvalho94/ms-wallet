package br.com.recargapay.usecase;

import br.com.recargapay.events.TransferFundsEvent;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferFunds {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final BalanceRepository balanceRepository;
    private final RetryTemplate retryTemplate;
    private final TransactionTemplate transactionTemplate;
    private final ApplicationEventPublisher eventPublisher;


    public Balance execute(UUID sourceWalletId, UUID targetWalletId, BigDecimal amount) {
        return retryTemplate.execute(context ->
                transactionTemplate.execute(status -> {
                            Wallet source = getWallet(sourceWalletId);
                            Wallet destination = getWallet(targetWalletId);

                            Balance sourceBalance = source.getBalance();
                            if (sourceBalance.getAmount().compareTo(amount) < 0) {
                                throw new InvalidTransactionAmountException("Insufficient funds in source wallet.");
                            }

                            Balance destinationBalance = destination.getBalance();

                            BigDecimal sourceNewBalance = sourceBalance.getAmount().subtract(amount);
                            BigDecimal destinationNewBalance = destinationBalance.getAmount().add(amount);

                            sourceBalance.setAmount(sourceNewBalance);
                            destinationBalance.setAmount(destinationNewBalance);


                            Transaction transaction = new Transaction();
                            transaction.setSource(source);
                            transaction.setDestination(destination);
                            transaction.setAmount(amount);
                            transaction.setType(TransactionType.TRANSFER);
                            transaction.setSourceResultingBalance(sourceNewBalance);
                            transaction.setDestinationResultingBalance(destinationNewBalance);
                            balanceRepository.saveAll(List.of(sourceBalance, destinationBalance));
                    Transaction savedTransaction = transactionRepository.save(transaction);
                    eventPublisher.publishEvent(new TransferFundsEvent(transaction.getId(),
                                    sourceWalletId,
                                    targetWalletId,
                                    amount,
                                    savedTransaction.getCreatedAt()));
                            return sourceBalance;
                        }
                ));

    }

    private Wallet getWallet(UUID targetWalletId) {
        return walletRepository.findById(targetWalletId).orElseThrow(() -> new WalletNotFoundException(targetWalletId));
    }
}
