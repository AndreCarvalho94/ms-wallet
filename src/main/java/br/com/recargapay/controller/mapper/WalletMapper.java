package br.com.recargapay.controller.mapper;

import br.com.recargapay.controller.dto.WalletRequest;
import br.com.recargapay.controller.dto.WalletResponse;
import br.com.recargapay.model.Wallet;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WalletMapper {
    Wallet toEntity(WalletRequest walletRequest);
    WalletResponse toResponse(Wallet wallet);
}