package br.com.recargapay.repository;

import br.com.recargapay.model.Transaction;
import br.com.recargapay.model.TransactionAggregates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query(value = """
            SELECT
                COALESCE(SUM(CASE WHEN t.type = 'DEPOSIT' AND t.destination.id = :walletId THEN t.amount ELSE 0 END), 0) AS totalDeposits,
                COALESCE(SUM(CASE WHEN t.type = 'WITHDRAW' AND t.source.id = :walletId THEN t.amount ELSE 0 END), 0) AS totalWithdrawals,
                COALESCE(SUM(CASE WHEN t.type = 'TRANSFER' AND t.destination.id = :walletId THEN t.amount ELSE 0 END), 0) AS totalTransfersIn,
                COALESCE(SUM(CASE WHEN t.type = 'TRANSFER' AND t.source.id = :walletId THEN t.amount ELSE 0 END), 0) AS totalTransfersOut
            FROM Transaction t
            WHERE (t.source.id = :walletId OR t.destination.id = :walletId)
              AND t.createdAt <= :upperLimitDate
            """)
    TransactionAggregates findAggregatedAmountsByWalletAndDate(UUID walletId, LocalDateTime upperLimitDate);
}
