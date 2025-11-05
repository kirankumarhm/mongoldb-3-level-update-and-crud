package com.example.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailsRequestDto {
    private String detailId;
    private String weight;
    private String dimensions;
    private String color;
    private String material;
    private ProductLogisticsRequestDto logistics; // NEW
}