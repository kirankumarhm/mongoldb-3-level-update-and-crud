package com.example.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductLogisticsRequestDto {
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Shipping weight must be greater than 0")
    private Double shippingWeightKg;
    
    private String volumeCubicCm;
    
    @Size(max = 100, message = "Country of origin cannot exceed 100 characters")
    private String countryOfOrigin;
    
    private String customsCode;
}
