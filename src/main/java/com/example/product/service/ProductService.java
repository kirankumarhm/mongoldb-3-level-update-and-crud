package com.example.product.service;

import com.example.product.dto.ProductRequestDto;
import com.example.product.dto.ProductResponseDto;
import com.example.product.model.Product;
import com.example.product.model.ProductDetails;
import com.example.product.model.ProductLogistics; // NEW
import com.example.product.repository.ProductRepository;
import com.example.product.mapper.ProductMapper;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final MongoTemplate mongoTemplate;
    private final GenericUpdateService<Product> genericUpdateService;
//    private final ProductMapper productMapper;

    @Autowired
    public ProductService(ProductRepository productRepository, MongoTemplate mongoTemplate, GenericUpdateService<Product> genericUpdateService) {
        this.productRepository = productRepository;
        this.mongoTemplate = mongoTemplate;
        this.genericUpdateService = genericUpdateService;
//        this.productMapper = productMapper;
        this.genericUpdateService.configure("productDetails", "detailId");
    }

    public ProductResponseDto saveProduct(ProductRequestDto productRequestDto) {
        Product product = ProductMapper.INSTANCE.toEntity(productRequestDto);
        Product preparedProduct = prepareProductForSave(product);
        Product savedProduct = productRepository.save(preparedProduct);
        return ProductMapper.INSTANCE.toResponseDto(savedProduct);
    }

    public Optional<ProductResponseDto> getProductById(String id) {
        return productRepository.findById(id)
                .map(ProductMapper.INSTANCE::toResponseDto);
    }

    public Optional<ProductResponseDto> getProductByIdWithProjection(String id, List<String> fields) {
        Query query = new Query(Criteria.where("id").is(id));
        fields.forEach(field -> query.fields().include(field));
        
        Product product = mongoTemplate.findOne(query, Product.class);
        return Optional.ofNullable(product).map(ProductMapper.INSTANCE::toResponseDto);
    }

    public List<ProductResponseDto> getAllProductsWithProjection(List<String> fields) {
        Query query = new Query();
        fields.forEach(field -> query.fields().include(field));

        List<Product> products = mongoTemplate.find(query, Product.class);
        return products.stream()
                .map(ProductMapper.INSTANCE::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductMapper.INSTANCE::toResponseDto)
                .collect(Collectors.toList());
    }

    public void deleteProduct(String id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        }
    }

    public boolean updateProductStatus(String id, String newStatus) {
        Query query = new Query(Criteria.where("id").is(id));
        Update update = new Update();

        if (!StringUtils.hasText(newStatus)) {
            update.unset("status");
        } else {
            update.set("status", newStatus);
        }

        UpdateResult result = mongoTemplate.updateFirst(query, update, Product.class);
        return result.getMatchedCount() > 0;
    }

    public boolean updateProductFields(String id, Map<String, Object> updates) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            return false;
        }

        Product productToUpdate = productOptional.get();
        Product updatedProduct = genericUpdateService.applyUpdates(productToUpdate, updates);
        Product cleanedProduct = prepareProductForSave(updatedProduct);
        productRepository.save(cleanedProduct);

        return true;
    }

    /**
     * Prepares the Product object for saving to MongoDB.
     * This method sets string fields that are empty or whitespace-only to null.
     * It also sets empty lists to null. This ensures that MongoDB does not store
     * these fields, making the document leaner and adhering to "store only fields
     * having values".
     *
     * @param product The Product object to prepare.
     * @return The prepared Product object.
     */
    private Product prepareProductForSave(Product product) {
        if (product == null) {
            return null;
        }

        // --- Main Product fields ---
        if (!StringUtils.hasText(product.getName())) product.setName(null);
        if (!StringUtils.hasText(product.getDescription())) product.setDescription(null);
        if (!StringUtils.hasText(product.getCategory())) product.setCategory(null);
        if (product.getTags() != null && product.getTags().isEmpty()) product.setTags(null);
        if (!StringUtils.hasText(product.getManufacturer())) product.setManufacturer(null);
        if (!StringUtils.hasText(product.getModelNumber())) product.setModelNumber(null);
        if (!StringUtils.hasText(product.getSku())) product.setSku(null);
        if (!StringUtils.hasText(product.getStatus())) product.setStatus(null);

        // --- Nested ProductDetails List ---
        if (product.getProductDetails() != null && !product.getProductDetails().isEmpty()) {
            List<ProductDetails> cleanedDetails = new ArrayList<>();
            for (ProductDetails details : product.getProductDetails()) {
                if (details != null) {
                    // Clean ProductDetails fields
                    if (!StringUtils.hasText(details.getWeight())) details.setWeight(null);
                    if (!StringUtils.hasText(details.getDimensions())) details.setDimensions(null);
                    if (!StringUtils.hasText(details.getColor())) details.setColor(null);
                    if (!StringUtils.hasText(details.getMaterial())) details.setMaterial(null);

                    // NEW: Clean ProductLogistics fields if present
                    if (details.getLogistics() != null) {
                        ProductLogistics logistics = details.getLogistics();
                        // Only clean String fields, Double can be null naturally
                        if (!StringUtils.hasText(logistics.getVolumeCubicCm())) logistics.setVolumeCubicCm(null);
                        if (!StringUtils.hasText(logistics.getCountryOfOrigin())) logistics.setCountryOfOrigin(null);
                        if (!StringUtils.hasText(logistics.getCustomsCode())) logistics.setCustomsCode(null);

                        // If all fields of ProductLogistics are null/empty, set logistics to null
                        if (logistics.getShippingWeightKg() == null &&
                            !StringUtils.hasText(logistics.getVolumeCubicCm()) &&
                            !StringUtils.hasText(logistics.getCountryOfOrigin()) &&
                            !StringUtils.hasText(logistics.getCustomsCode())) {
                            details.setLogistics(null);
                        }
                    }

                    // Add ProductDetails to cleaned list only if it has meaningful data
                    if (StringUtils.hasText(details.getDetailId()) &&
                        (StringUtils.hasText(details.getWeight()) ||
                         StringUtils.hasText(details.getDimensions()) ||
                         StringUtils.hasText(details.getColor()) ||
                         StringUtils.hasText(details.getMaterial()) ||
                         details.getLogistics() != null)) { // Check if logistics object is not null
                        cleanedDetails.add(details);
                    }
                }
            }
            product.setProductDetails(cleanedDetails.isEmpty() ? null : cleanedDetails);
        } else {
            product.setProductDetails(null);
        }

        return product;
    }
}