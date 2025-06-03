package br.com.recargapay.exceptions;

public class InvalidTransactionAmountException extends RuntimeException{

    public InvalidTransactionAmountException(){
        super("Invalid transaction amount. Amount must be greater than zero.");
    }

    public InvalidTransactionAmountException(String message) {
        super(message);
    }
}
