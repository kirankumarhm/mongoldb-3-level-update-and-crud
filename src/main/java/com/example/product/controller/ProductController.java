package com.example.product.controller;

import com.example.product.dto.ProductRequestDto;
import com.example.product.dto.ProductResponseDto;
import com.example.product.service.ProductService;
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
@RestController // Marks this class as a Spring REST Controller
@RequestMapping("/api/products") // Base path for all endpoints in this controller
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
    @PostMapping // Maps HTTP POST requests to /api/products
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody ProductRequestDto productRequestDto) {
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
    @GetMapping("/{id}") // Maps HTTP GET requests to /api/products/{id}
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable String id) {
        return productService.getProductById(id)
                .map(productResponseDto -> new ResponseEntity<>(productResponseDto, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
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
    public ResponseEntity<ProductResponseDto> getProductByIdWithProjection(
            @PathVariable String id,
            @RequestParam(required = false) String fields) {

        List<String> fieldList = (fields != null && !fields.isEmpty()) ?
                Arrays.asList(fields.split(",")).stream().map(String::trim).collect(Collectors.toList()) :
                List.of(); // Empty list if no fields specified, service will fetch all

        return productService.getProductByIdWithProjection(id, fieldList)
                .map(productResponseDto -> new ResponseEntity<>(productResponseDto, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Retrieves all products from the database. Returns a list of Response DTOs.
     *
     * @return A ResponseEntity containing a list of all ProductResponseDto objects.
     */
    @GetMapping // Maps HTTP GET requests to /api/products
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
    public ResponseEntity<List<ProductResponseDto>> getAllProductsWithProjection(
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
    @DeleteMapping("/{id}") // Maps HTTP DELETE requests to /api/products/{id}
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Partially updates the 'status' field of a product.
     *
     * @param id The ID of the product to update.
     * @param updates A map containing the "status" field and its new value.
     * @return A ResponseEntity with HTTP status 200 (OK) on successful update,
     * or 404 (Not Found) if the product does not exist.
     */
    @PatchMapping("/{id}/status") // Maps HTTP PATCH requests to /api/products/{id}/status
    public ResponseEntity<Void> updateProductStatus(@PathVariable String id, @RequestBody Map<String, String> updates) {
        String newStatus = updates.get("status");
        if (newStatus == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Status field is required
        }
        boolean updated = productService.updateProductStatus(id, newStatus);
        if (updated) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
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
    @PatchMapping("/{id}") // Generic PATCH endpoint for /api/products/{id}
    public ResponseEntity<Void> patchProduct(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        if (updates == null || updates.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // No updates provided
        }

        boolean updated = productService.updateProductFields(id, updates);
        if (updated) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

