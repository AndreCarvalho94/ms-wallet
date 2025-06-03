package br.com.recargapay.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record WalletCreation(UUID walletId, UUID userId, BigDecimal balance, LocalDateTime createdAt) {
}

