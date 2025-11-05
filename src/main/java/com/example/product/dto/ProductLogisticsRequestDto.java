package com.example.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductLogisticsRequestDto {
    private Double shippingWeightKg;
    private String volumeCubicCm;
    private String countryOfOrigin;
    private String customsCode;
}