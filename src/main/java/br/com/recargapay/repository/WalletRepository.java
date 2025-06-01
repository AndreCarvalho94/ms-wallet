package br.com.recargapay.repository;

import br.com.recargapay.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {


}
