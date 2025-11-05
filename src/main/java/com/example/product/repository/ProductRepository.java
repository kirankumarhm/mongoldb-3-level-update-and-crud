package com.example.product.repository;

import com.example.product.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Product documents.
 * Extends MongoRepository to provide standard CRUD operations for Product.
 */
@Repository // Marks this interface as a Spring repository component
public interface ProductRepository extends MongoRepository<Product, String> {
    // Custom query methods can be added here if needed,
    // e.g., Product findByName(String name);
	
	ProductInterface findByName(String name);
}


interface ProductInterface {
	String getName();
	String getDescription();
}