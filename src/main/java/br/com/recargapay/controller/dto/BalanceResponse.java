package br.com.recargapay.controller.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BalanceResponse {

    private BigDecimal amount;
    private LocalDateTime updatedAt;
}
