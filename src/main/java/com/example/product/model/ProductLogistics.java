package com.example.product.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents logistics information for a product detail.
 * This is nested within ProductDetails.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductLogistics {
    private Double shippingWeightKg;
    private String volumeCubicCm;
    private String countryOfOrigin;
    private String customsCode; // New field for demonstration
}