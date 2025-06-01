package br.com.recargapay.controller.mapper;

import br.com.recargapay.controller.dto.WalletRequest;
import br.com.recargapay.entity.Wallet;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WalletMapper {
    Wallet toEntity(WalletRequest walletRequest);
}