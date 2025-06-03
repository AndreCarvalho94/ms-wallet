package br.com.recargapay.controller.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawFundsRequest {

    private BigDecimal amount;
}
