package br.com.recargapay.exceptions;

import java.util.UUID;

public class WalletNotFoundException extends RuntimeException {

    public WalletNotFoundException(UUID walletId) {
        super("Wallet not found with ID: " + walletId);
    }
}
