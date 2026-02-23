package com.example.product.controller;

import com.example.product.dto.ProductRequestDto;
import com.example.product.dto.ProductResponseDto;
import com.example.product.exception.InvalidUpdateException;
import com.example.product.exception.ProductNotFoundException;
import com.example.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for Product management.
 * Exposes endpoints for creating, retrieving, and deleting products,
 * and performing partial updates.
 */
@RestController
@RequestMapping("/api/products")
@Tag(name = "Product Management", description = "APIs for managing products with nested collections")
public class ProductController {

    private final ProductService productService;

    @Autowired // Injects ProductService dependency
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Creates a new product.
     * The incoming ProductRequestDto will be processed by the service to ensure
     * only fields with values are persisted.
     *
     * @param productRequestDto The ProductRequestDto object received in the request body.
     * @return A ResponseEntity containing the saved ProductResponseDto and HTTP status 201 (Created).
     */
    @PostMapping
    @Operation(summary = "Create a new product", description = "Creates a new product with optional nested details and logistics")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully",
                    content = @Content(schema = @Schema(implementation = ProductResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody ProductRequestDto productRequestDto) {
        ProductResponseDto savedProductDto = productService.saveProduct(productRequestDto);
        return new ResponseEntity<>(savedProductDto, HttpStatus.CREATED);
    }

    /**
     * Retrieves a product by its ID. Returns a Response DTO.
     *
     * @param id The ID of the product to retrieve.
     * @return An Optional containing the ProductResponseDto and HTTP status 200 (OK),
     * or 404 (Not Found) if the product does not exist.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieves a product by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found",
                    content = @Content(schema = @Schema(implementation = ProductResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ProductResponseDto> getProductById(
            @Parameter(description = "Product ID", required = true) @PathVariable String id) {
        ProductResponseDto product = productService.getProductById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return ResponseEntity.ok(product);
    }

    /**
     * Retrieves a product by its ID, including only the specified fields (projection).
     * Example: GET /api/products/{id}/projected?fields=name,price,productDetails.color
     *
     * @param id The ID of the product to retrieve.
     * @param fields A comma-separated string of field names to include in the response.
     * @return An Optional containing the ProductResponseDto with projected fields if found, or empty otherwise.
     */
    @GetMapping("/{id}/projected")
    @Operation(summary = "Get product with field projection", 
               description = "Retrieves specific fields of a product. Example: fields=name,price,productDetails.color")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found with projected fields"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ProductResponseDto> getProductByIdWithProjection(
            @Parameter(description = "Product ID", required = true) @PathVariable String id,
            @Parameter(description = "Comma-separated field names", example = "name,price,category")
            @RequestParam(required = false) String fields) {

        List<String> fieldList = (fields != null && !fields.isEmpty()) ?
                Arrays.asList(fields.split(",")).stream().map(String::trim).collect(Collectors.toList()) :
                List.of();

        ProductResponseDto product = productService.getProductByIdWithProjection(id, fieldList)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return ResponseEntity.ok(product);
    }

    /**
     * Retrieves all products from the database. Returns a list of Response DTOs.
     *
     * @return A ResponseEntity containing a list of all ProductResponseDto objects.
     */
    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieves all products from the database")
    @ApiResponse(responseCode = "200", description = "List of products retrieved successfully")
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        List<ProductResponseDto> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    /**
     * Retrieves all products, including only the specified fields (projection).
     * Example: GET /api/products/projected?fields=name,category,tags
     *
     * @param fields A comma-separated string of field names to include in the response.
     * @return A ResponseEntity containing a list of ProductResponseDto objects with projected fields.
     */
    @GetMapping("/projected")
    @Operation(summary = "Get all products with field projection",
               description = "Retrieves all products with only specified fields")
    @ApiResponse(responseCode = "200", description = "Products retrieved with projected fields")
    public ResponseEntity<List<ProductResponseDto>> getAllProductsWithProjection(
            @Parameter(description = "Comma-separated field names", example = "name,price")
            @RequestParam(required = false) String fields) {

        List<String> fieldList = (fields != null && !fields.isEmpty()) ?
                Arrays.asList(fields.split(",")).stream().map(String::trim).collect(Collectors.toList()) :
                List.of(); // Empty list if no fields specified, service will fetch all

        List<ProductResponseDto> products = productService.getAllProductsWithProjection(fieldList);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }


    /**
     * Deletes a product by its ID.
     *
     * @param id The ID of the product to delete.
     * @return A ResponseEntity with HTTP status 204 (No Content) on successful deletion,
     * or 404 (Not Found) if the product does not exist.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product", description = "Deletes a product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID", required = true) @PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Partially updates the 'status' field of a product.
     *
     * @param id The ID of the product to update.
     * @param updates A map containing the "status" field and its new value.
     * @return A ResponseEntity with HTTP status 200 (OK) on successful update,
     * or 404 (Not Found) if the product does not exist.
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update product status", description = "Updates only the status field of a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status value"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Void> updateProductStatus(
            @Parameter(description = "Product ID", required = true) @PathVariable String id,
            @RequestBody Map<String, String> updates) {
        String newStatus = updates.get("status");
        if (newStatus == null) {
            throw new InvalidUpdateException("Status field is required");
        }
        productService.updateProductStatus(id, newStatus);
        return ResponseEntity.ok().build();
    }

    /**
     * Performs a generic partial update on a product.
     * Accepts a map of field names to new values. This method now supports
     * updating specific elements within `List<ProductDetails>` using
     * a dot notation like "productDetails.detailIdValue.fieldName".
     *
     * @param id The ID of the product to update.
     * @param updates A map where keys are field names (e.g., "name", "price", "status", "productDetails.spec-A.color")
     * and values are the new values.
     * @return A ResponseEntity with HTTP status 200 (OK) on successful update,
     * or 404 (Not Found) if the product does not exist, or 400 (Bad Request) if no updates provided.
     */
    @PatchMapping("/{id}")
    @Operation(summary = "Partial update product", 
               description = "Updates specific fields of a product including nested fields. Supports dot notation for nested updates (e.g., productDetails.spec-1.color)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid update data"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Void> patchProduct(
            @Parameter(description = "Product ID", required = true) @PathVariable String id,
            @RequestBody Map<String, Object> updates) {
        if (updates == null || updates.isEmpty()) {
            throw new InvalidUpdateException("No updates provided");
        }
        productService.updateProductFields(id, updates);
        return ResponseEntity.ok().build();
    }
}

