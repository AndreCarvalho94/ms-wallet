package br.com.recargapay.model;

import java.math.BigDecimal;

public interface TransactionAggregates {
    BigDecimal getTotalDeposits();
    BigDecimal getTotalWithdrawals();
    BigDecimal getTotalTransfersIn();
    BigDecimal getTotalTransfersOut();
}