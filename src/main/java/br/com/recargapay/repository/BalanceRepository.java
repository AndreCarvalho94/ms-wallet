package br.com.recargapay.repository;

import br.com.recargapay.model.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BalanceRepository extends JpaRepository<Balance, UUID> {

    boolean existsByWalletId(UUID walletId);
    Optional<Balance> findByWalletId(UUID walletId);

}
