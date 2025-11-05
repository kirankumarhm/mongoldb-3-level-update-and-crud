package com.example.product.mapper;

import com.example.product.dto.ProductRequestDto;
import com.example.product.dto.ProductResponseDto;
import com.example.product.dto.ProductDetailsRequestDto;
import com.example.product.dto.ProductDetailsResponseDto;
import com.example.product.dto.ProductLogisticsRequestDto; // NEW
import com.example.product.dto.ProductLogisticsResponseDto; // NEW
import com.example.product.model.Product;
import com.example.product.model.ProductDetails;
import com.example.product.model.ProductLogistics; // NEW
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    // --- Mappings for Product (Main Entity) ---
    ProductResponseDto toResponseDto(Product product);
    Product toEntity(ProductRequestDto productRequestDto);

    // --- Mappings for ProductDetails (Nested Entity) ---
    // MapStruct will automatically map the 'logistics' field because names match
    ProductDetailsResponseDto toProductDetailsResponseDto(ProductDetails productDetails);
    ProductDetails toProductDetailsEntity(ProductDetailsRequestDto productDetailsRequestDto);

    // --- Mappings for ProductLogistics (Double-Nested Entity) ---
    ProductLogisticsResponseDto toProductLogisticsResponseDto(ProductLogistics productLogistics); // NEW
    ProductLogistics toProductLogisticsEntity(ProductLogisticsRequestDto productLogisticsRequestDto); // NEW

    // --- Mappings for Lists ---
    List<ProductDetailsResponseDto> toProductDetailsResponseDtoList(List<ProductDetails> productDetails);
    List<ProductDetails> toProductDetailsEntityList(List<ProductDetailsRequestDto> productDetailsRequestDtos);
    // No explicit list mapping needed for ProductLogistics, as it's a single object within ProductDetails
}