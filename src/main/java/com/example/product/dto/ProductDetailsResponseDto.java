package com.example.product.dto;

import lombok.Value;

@Value
public class ProductDetailsResponseDto {
    String detailId;
    String weight;
    String dimensions;
    String color;
    String material;
    ProductLogisticsResponseDto logistics; // NEW
}