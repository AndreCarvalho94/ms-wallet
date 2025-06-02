package br.com.recargapay.model;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class DailyBalance {

    private BigDecimal amout = BigDecimal.ZERO;
}
