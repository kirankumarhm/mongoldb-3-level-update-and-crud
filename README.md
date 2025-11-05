# mongoldb-3-level-update-and-crud# Overwrite 
Spring Boot example demonstrating:
- CRUD operations on a Product document stored in MongoDB.
- Storing only fields with values (cleans empty/blank fields before save).
- Partial updates (PATCH) including: top-level fields, a single "status" field, and deep updates into list elements using a unique ID (3-level nested updates).
- Projection queries (fetching only selected fields).

This repository is intentionally small and shows how to:
- Model nested objects (Product -> List<ProductDetails> -> ProductLogistics).
- Apply generic partial updates to nested list elements by a unique ID.
- Use MapStruct + Lombok for DTO mapping.

Key files and classes
- REST controller: [`com.example.product.controller.ProductController`](src/main/java/com/example/product/controller/ProductController.java)
- Service: [`com.example.product.service.ProductService`](src/main/java/com/example/product/service/ProductService.java)
- Generic partial-updater: [`com.example.product.service.GenericUpdateService`](src/main/java/com/example/product/service/GenericUpdateService.java)
- Entity/model: [`com.example.product.model.Product`](src/main/java/com/example/product/model/Product.java)
- Nested models: [`ProductDetails`](src/main/java/com/example/product/model/ProductDetails.java), [`ProductLogistics`](src/main/java/com/example/product/model/ProductLogistics.java)
- DTOs: [`ProductRequestDto`](src/main/java/com/example/product/dto/ProductRequestDto.java), [`ProductResponseDto`](src/main/java/com/example/product/dto/ProductResponseDto.java)
- Mapper (MapStruct): [`com.example.product.mapper.ProductMapper`](src/main/java/com/example/product/mapper/ProductMapper.java)
- Repository: [`com.example.product.repository.ProductRepository`](src/main/java/com/example/product/repository/ProductRepository.java)
- Mongo config / props: [`src/main/resources/application.properties`](src/main/resources/application.properties) (defaults to mongodb://localhost:27017/productdb)
- Build config: [`pom.xml`](pom.xml) (uses Java 21 and Spring Boot 3.4.x)

Tech stack
- Java 21
- Spring Boot 3.x (Web, Spring Data MongoDB)
- MongoDB (tested against local MongoDB)
- MapStruct + Lombok for DTO mapping and model convenience
- Maven

Prerequisites
- Java 21 JDK
- Maven 3.8+
- A running MongoDB instance (default: mongodb://localhost:27017/productdb) — can be local or Docker.

Default configuration
- application.properties (defaults included in repo):
  - spring.data.mongodb.uri = mongodb://localhost:27017/productdb
  - spring.data.mongodb.auto-index-creation=true

Building & running

1) Start MongoDB (local or Docker). Example using Docker:
```bash
docker run --name mongodb -p 27017:27017 -d mongo:7

2. Build with Maven:
```
mvn -U clean package
```

3. Run (option A: maven plugin):
```
mvn spring-boot:run
```
To override Mongo URI at runtime:
```
# Example using mvn spring-boot:run with override
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.data.mongodb.uri=mongodb://myhost:27017/productdb"
# or with jar:
java -jar target/*.jar --spring.data.mongodb.uri=mongodb://myhost:27017/productdb
```

API endpoints

Base path: /api/products

- POST /api/products
    - Create a product.
    - Request body: ProductRequestDto shape (see below).
    - Response: 201 Created with ProductResponseDto.
- GET /api/products
    - Get all products.
    - Response: 200 with list of ProductResponseDto.
- GET /api/products/{id}
    - Get single product by id.
- GET /api/products/{id}/projected?fields=comma,separated,paths
    - Projection: include only the specified fields in the returned document.
    - Example: GET /api/products/64a2b.../projected?fields=name,price,productDetails.color
- GET /api/products/projected?fields=...
    - Get all products, but only include listed fields.
- DELETE /api/products/{id}
    - Remove a product.
- PATCH /api/products/{id}/status
    - Partial update for the status field.
    - Request body: JSON map containing "status".
    - Example request:
```
        { "status": "Discontinued" }
        ```
- PATCH /api/products/{id}
    - Generic partial update. Accepts a JSON map of updates.
    - Supports:
        - Top-level fields, e.g. "name": "New Name".
        - Deep updates into elements of productDetails using dot-path notation that identifies a list element by its detailId.
            - Format: "productDetails.<detailId>.<nestedPath>", where <nestedPath> can be color or deeper paths like logistics.shippingWeightKg.
            - Example: set shipping weight inside a product detail identified by DETAIL-1:
                - Key: productDetails.DETAIL-1.logistics.shippingWeightKg
                - Value: 2.5

JSON shapes / examples

Sample ProductRequestDto (create):


```
{
  "name": "Example Widget",
  "description": "An example product",
  "price": 19.99,
  "category": "gadgets",
  "available": true,
  "tags": ["featured","sale"],
  "manufacturer": "Acme",
  "modelNumber": "W-1000",
  "sku": "ACME-W1000",
  "status": "Active",
  "productDetails": [
    {
      "detailId": "DETAIL-1",
      "weight": "200g",
      "dimensions": "10x5x2",
      "color": "red",
      "material": "plastic",
      "logistics": {
        "shippingWeightKg": 0.2,
        "volumeCubicCm": "1000",
        "countryOfOrigin": "CN",
        "customsCode": "ABC123"
      }
    },
    {
      "detailId": "DETAIL-2",
      "weight": "300g",
      "dimensions": "12x6x3",
      "color": "blue",
      "material": "metal"
    }
  ]
}
```


Sample ProductResponseDto (partial):
```
{
  "id": "650b...",
  "name": "Example Widget",
  "price": 19.99,
  "status": "Active",
  "productDetails": [
    {
      "detailId": "DETAIL-1",
      "weight": "200g",
      "logistics": {
        "shippingWeightKg": 0.2,
        "volumeCubicCm": "1000",
        "countryOfOrigin": "CN",
        "customsCode": "ABC123"
      }
    }
  ]
}
```

Example curl requests

- Create:

```
curl -s -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d @product-create.json
```
(where product-create.json contains the sample request above)

- Get by id:
```
curl http://localhost:8080/api/products/{id}
```

 - Projection for single product:
```
curl "http://localhost:8080/api/products/{id}/projected?fields=name,price,productDetails.color"
```

- Update status:
```
curl -X PATCH http://localhost:8080/api/products/{id}/status \
  -H "Content-Type: application/json" \
  -d '{"status":"Discontinued"}'
```

- Generic deep update (update logistics.shippingWeightKg of productDetails element with detailId=DETAIL-1):

```
curl -X PATCH http://localhost:8080/api/products/{id} \
  -H "Content-Type: application/json" \
  -d '{"productDetails.DETAIL-1.logistics.shippingWeightKg": 2.5}'
```

Notes about partial-saving behavior

- Before saving, ProductService runs prepareProductForSave to:
    - Convert blank/empty strings to null.
    - Convert empty lists to null.
    - Clean nested ProductDetails and ProductLogistics to avoid storing empty sub-documents.

- This results in MongoDB documents that store only meaningful fields.
