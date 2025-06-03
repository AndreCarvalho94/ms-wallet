package br.com.recargapay.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransferFundsEvent(UUID transactionId, UUID sourceWalletId, UUID destinationWalletId, BigDecimal amount, LocalDateTime createdAt) {
}
