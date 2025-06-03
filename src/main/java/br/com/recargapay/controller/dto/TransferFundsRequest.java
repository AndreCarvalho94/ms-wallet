package br.com.recargapay.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class TransferFundsRequest {
    private UUID sourceWalletId;
    private UUID destinationWalletId;
    private BigDecimal amount;
}
