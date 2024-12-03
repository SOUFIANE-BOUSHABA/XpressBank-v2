package org.example.version2_xpresbank.Exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String message) {

        super(message);
    }
}
