package br.com.recargapay.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DepositFundsRequest {

    private BigDecimal amount;


}
