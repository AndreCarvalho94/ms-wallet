package br.com.recargapay.controller.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class WalletResponse {

    private UUID id;
    private BalanceResponse balance;

}
