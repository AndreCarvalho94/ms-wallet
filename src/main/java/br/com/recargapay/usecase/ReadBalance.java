package br.com.recargapay.usecase;

import br.com.recargapay.exceptions.WalletNotFoundException;
import br.com.recargapay.model.Balance;
import br.com.recargapay.model.DailyBalance;
import br.com.recargapay.model.TransactionAggregates;
import br.com.recargapay.repository.BalanceRepository;
import br.com.recargapay.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReadBalance {

    private final BalanceRepository repository;
    private final TransactionRepository transactionRepository;

    public Balance execute(UUID walletId) {
        Optional<Balance> wallet = repository.findByWalletId(walletId);
        return wallet.orElseThrow(() -> new WalletNotFoundException(walletId));
    }

    public DailyBalance execute(UUID walletId, LocalDate date) {
        if(!repository.existsByWalletId(walletId)){
            throw new WalletNotFoundException(walletId);
        }
        TransactionAggregates aggregates = transactionRepository.findAggregatedAmountsByWalletAndDate(walletId, date.atTime(23, 59, 59));
        DailyBalance dailyBalance = new DailyBalance();
        BigDecimal totalTransfers = aggregates.getTotalTransfersIn().subtract(aggregates.getTotalTransfersOut());
        BigDecimal totalDeposits = aggregates.getTotalDeposits().subtract(aggregates.getTotalWithdrawals());
        dailyBalance.setAmount(totalTransfers.add(totalDeposits));
        return dailyBalance;
    }

}
