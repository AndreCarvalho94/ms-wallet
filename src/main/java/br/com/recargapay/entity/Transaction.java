package br.com.recargapay.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@Table(name = "transactions")
public class Transaction {
    private UUID id = UUID.randomUUID();

    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "source_account_id")
    private Wallet source;

    @ManyToOne
    @JoinColumn(name = "destination_account_id")
    private Wallet destination;

    private TransactionType type;


}
