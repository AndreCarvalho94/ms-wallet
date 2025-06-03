package br.com.recargapay.configuration.idempotency;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdempotencyValue {
    private int httpStatus;
    private String body;
    private String contentType;
}
