package com.example.product.dto;

import lombok.Value;

@Value
public class ProductLogisticsResponseDto {
    Double shippingWeightKg;
    String volumeCubicCm;
    String countryOfOrigin;
    String customsCode;
}
