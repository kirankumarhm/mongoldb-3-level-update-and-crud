package com.example.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// Using Lombok's @Data for convenience in deserialization (mutable)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {
    // ID is typically not sent in a POST request, but might be in a PUT if client specifies it
    // private String id;
    private String name;
    private String description;
    private Double price;
    private String category;
    private Boolean available;
    private List<String> tags;
    private String manufacturer;
    private String modelNumber;
    private String sku;
    private String status;
    private List<ProductDetailsRequestDto> productDetails; // Nested Request DTO list
}