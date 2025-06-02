package br.com.recargapay.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@Entity
@Table(name = "wallets")
@ToString(exclude = "balance")
public class Wallet {

    @Id
    private UUID id = UUID.randomUUID();

    private UUID userId;

    @OneToOne(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private Balance balance;
}