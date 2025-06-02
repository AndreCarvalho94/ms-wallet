package br.com.recargapay.controller.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ErrorDto {
    private String message;
    private Integer status;
    private LocalDateTime timestamp;
    private List<String> errors;
}
