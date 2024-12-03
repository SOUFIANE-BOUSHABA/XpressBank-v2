package org.example.version2_xpresbank.Exception;

public class CreditRequestNotFoundException extends RuntimeException {
    public CreditRequestNotFoundException(String message) {
        super(message);
    }
}
