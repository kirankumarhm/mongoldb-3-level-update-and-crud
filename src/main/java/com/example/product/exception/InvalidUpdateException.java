package com.example.product.exception;

/**
 * Exception thrown when an invalid update operation is attempted.
 */
public class InvalidUpdateException extends RuntimeException {
    
    public InvalidUpdateException(String message) {
        super(message);
    }
    
    public InvalidUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
