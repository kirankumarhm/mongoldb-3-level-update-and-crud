package com.example.product.exception;

/**
 * Exception thrown when a product is not found in the database.
 */
public class ProductNotFoundException extends RuntimeException {
    
    public ProductNotFoundException(String id) {
        super("Product not found with id: " + id);
    }
    
    public ProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
