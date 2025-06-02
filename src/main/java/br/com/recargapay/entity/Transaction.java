package br.com.recargapay.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@Table(name = "transactions")
@ToString(exclude = "balance")
public class Transaction {

    @Id
    private UUID id = UUID.randomUUID();

    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "balance_id", nullable = false)
    private Balance balance;

    @Enumerated(EnumType.STRING)
    private TransactionType type;
}