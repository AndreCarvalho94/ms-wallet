package br.com.recargapay.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record WithdrawFundsEvent (UUID transactionId, UUID walletId, BigDecimal amount, LocalDateTime createdAt){
}
