package com.insurance.quote.exception;

/**
 * Exception thrown when a quote operation is attempted in an invalid state
 */
public class InvalidQuoteStateException extends RuntimeException {
    
    public InvalidQuoteStateException() {
        super();
    }

    public InvalidQuoteStateException(String message) {
        super(message);
    }

    public InvalidQuoteStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidQuoteStateException(Throwable cause) {
        super(cause);
    }
}