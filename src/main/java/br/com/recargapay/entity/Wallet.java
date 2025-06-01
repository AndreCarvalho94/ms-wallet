package br.com.recargapay.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "wallets")
public class Wallet {

    private UUID id = UUID.randomUUID();

    private UUID userId;
    @OneToOne(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private Balance balance = new Balance();
}
