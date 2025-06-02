package br.com.recargapay.controller.mapper;

import br.com.recargapay.controller.dto.BalanceResponse;
import br.com.recargapay.model.Balance;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BalanceMapper {

    BalanceResponse toResponse(Balance balance);
}
