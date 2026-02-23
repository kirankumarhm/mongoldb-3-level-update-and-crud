package com.example.product.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailsRequestDto {
    
    @NotBlank(message = "Detail ID is required")
    private String detailId;
    
    private String weight;
    private String dimensions;
    private String color;
    private String material;
    
    @Valid
    private ProductLogisticsRequestDto logistics;
}
