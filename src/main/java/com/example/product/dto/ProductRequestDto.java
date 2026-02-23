package com.example.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Product creation/update request")
public class ProductRequestDto {
    
    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    @Schema(description = "Product name", example = "Laptop", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Schema(description = "Product description", example = "High-performance laptop")
    private String description;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Schema(description = "Product price", example = "999.99", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double price;
    
    @NotBlank(message = "Category is required")
    @Schema(description = "Product category", example = "Electronics", requiredMode = Schema.RequiredMode.REQUIRED)
    private String category;
    
    @Schema(description = "Product availability status", example = "true")
    private Boolean available;
    
    @Schema(description = "Product tags", example = "[\"laptop\", \"computer\"]")
    private List<String> tags;
    
    @Size(max = 100, message = "Manufacturer name cannot exceed 100 characters")
    @Schema(description = "Manufacturer name", example = "TechCorp")
    private String manufacturer;
    
    @Schema(description = "Model number", example = "TC-2024")
    private String modelNumber;
    
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "SKU must contain only uppercase letters, numbers, and hyphens")
    @Schema(description = "Stock Keeping Unit", example = "LAPTOP-001")
    private String sku;
    
    @Schema(description = "Product status", example = "Active")
    private String status;
    
    @Valid
    @Schema(description = "List of product details with nested logistics")
    private List<ProductDetailsRequestDto> productDetails;
}
