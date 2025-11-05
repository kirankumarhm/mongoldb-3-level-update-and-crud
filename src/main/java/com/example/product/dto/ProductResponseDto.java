package com.example.product.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {
    String id; // ID is always included in responses
    String name;
    String description;
    Double price;
    String category;
    Boolean available;
    List<String> tags;
    String manufacturer;
    String modelNumber;
    String sku;
    String status;
    List<ProductDetailsResponseDto> productDetails; // Nested Response DTO list
}
