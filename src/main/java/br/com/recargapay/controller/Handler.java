package br.com.recargapay.controller;

import br.com.recargapay.controller.dto.ErrorDto;
import br.com.recargapay.exceptions.WalletNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class Handler {


    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<ErrorDto> handleWalletNotFoundException(WalletNotFoundException ex) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setMessage(ex.getMessage());
        errorDto.setStatus(HttpStatus.NOT_FOUND.value());
        errorDto.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDto);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorDto> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setMessage("The provided parameter is invalid: " + ex.getValue());
        errorDto.setStatus(HttpStatus.BAD_REQUEST.value());
        errorDto.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }
}
