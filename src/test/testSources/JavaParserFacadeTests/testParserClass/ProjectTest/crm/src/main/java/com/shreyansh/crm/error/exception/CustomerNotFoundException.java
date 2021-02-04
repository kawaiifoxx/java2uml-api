package com.shreyansh.crm.error.exception;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException() {
    }

    public CustomerNotFoundException(String message) {
        super(message);
    }

    public CustomerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public CustomerNotFoundException(Throwable cause) {
        super(cause);
    }
}
