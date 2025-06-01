package br.com.recargapay.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "balances")
public class Balance {

    private UUID id = UUID.randomUUID();

    private BigDecimal amount;

    @OneToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    List<Transaction> transactions = new ArrayList<>();


    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Version
    private Integer version;
}
