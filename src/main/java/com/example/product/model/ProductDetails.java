package com.example.product.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the detailed specifications of a product.
 * This is a nested object within the Product domain.
 */
@Data // Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all fields
public class ProductDetails {
	private String detailId;
    private String weight;
    private String dimensions;
    private String color;
    private String material;
    
    private ProductLogistics logistics; // NEW: Nested logistics information
}   