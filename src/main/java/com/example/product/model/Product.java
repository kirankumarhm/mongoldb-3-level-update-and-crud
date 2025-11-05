package com.example.product.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Represents a product in the inventory.
 * This is the main domain object with 10 fields and a nested object.
 */
@Data // Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all fields
@Builder // Provides a builder pattern for object creation
@Document(collection = "products") // Maps this class to a MongoDB collection named "products"
public class Product {

    @Id // Marks this field as the primary identifier for the MongoDB document
    private String id; // MongoDB's _id field

    private String name;
    private String description;
    private Double price; // Using Double wrapper to allow for null if price is not applicable
    private String category;
    private Boolean available; // Using Boolean wrapper to allow for null if availability is unknown
    private List<String> tags; // List of tags associated with the product
    private String manufacturer;
    private String modelNumber;
    private String sku; // Stock Keeping Unit - a unique identifier for a product
    private String status; // New field for product status (e.g., "Active", "Discontinued")
    // Nested object
    private List<ProductDetails> productDetails; // The nested object containing additional details
}