package br.com.recargapay.controller.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class WalletRequest {

    @NotNull
    private UUID userId;
}
