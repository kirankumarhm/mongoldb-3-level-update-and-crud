# Strategy Pattern Implementation

## Overview

The `GenericUpdateService` now uses the **Strategy Pattern** to handle different types of field updates. This makes the code more maintainable, testable, and extensible.

## Architecture

### Strategy Interface

```java
public interface UpdateStrategy {
    boolean canHandle(String fieldPath);
    void applyUpdate(BeanWrapper beanWrapper, String fieldPath, Object value);
}
```

### Concrete Strategies

1. **SimpleFieldUpdateStrategy** - Handles top-level fields
   - Example: `name`, `price`, `status`
   - No dots in field path

2. **NestedFieldUpdateStrategy** - Handles nested object fields
   - Example: `address.city`, `metadata.version`
   - Contains dots but not list notation

3. **ListElementUpdateStrategy** - Handles list element updates
   - Example: `productDetails.spec-A.logistics.shippingWeightKg`
   - Configured with list field name and ID field name

## Benefits

### 1. Single Responsibility Principle
Each strategy handles one type of update:
- Simple fields
- Nested objects
- List elements

### 2. Open/Closed Principle
Add new update types without modifying existing code:
```java
@Component
public class CustomUpdateStrategy implements UpdateStrategy {
    @Override
    public boolean canHandle(String fieldPath) {
        return fieldPath.matches("custom.*");
    }
    
    @Override
    public void applyUpdate(BeanWrapper beanWrapper, String fieldPath, Object value) {
        // Custom logic
    }
}
```

### 3. Testability
Each strategy can be unit tested independently:
```java
@Test
void testSimpleFieldUpdate() {
    SimpleFieldUpdateStrategy strategy = new SimpleFieldUpdateStrategy();
    assertTrue(strategy.canHandle("name"));
    assertFalse(strategy.canHandle("address.city"));
}
```

### 4. Maintainability
- Clear separation of concerns
- Easy to debug specific update types
- Reduced complexity in main service

## Usage

### Configuration

```java
@Service
public class ProductService {
    private final GenericUpdateService<Product> updateService;
    
    @PostConstruct
    public void init() {
        updateService.configure("productDetails", "detailId");
    }
}
```

### Applying Updates

```java
Map<String, Object> updates = Map.of(
    "name", "New Product Name",                                    // SimpleFieldUpdateStrategy
    "metadata.version", "2.0",                                     // NestedFieldUpdateStrategy
    "productDetails.spec-A.logistics.shippingWeightKg", 5.5       // ListElementUpdateStrategy
);

Product updated = updateService.applyUpdates(product, updates);
```

## Strategy Selection Order

Strategies are checked in this order:
1. **ListElementUpdateStrategy** (most specific)
2. **NestedFieldUpdateStrategy** (medium specificity)
3. **SimpleFieldUpdateStrategy** (fallback)

## Adding Custom Strategies

### Step 1: Create Strategy Class

```java
@Component
public class DateFieldUpdateStrategy implements UpdateStrategy {
    
    @Override
    public boolean canHandle(String fieldPath) {
        return fieldPath.endsWith("Date") || fieldPath.endsWith("Timestamp");
    }
    
    @Override
    public void applyUpdate(BeanWrapper beanWrapper, String fieldPath, Object value) {
        // Convert string to Date/LocalDateTime
        LocalDateTime date = LocalDateTime.parse(value.toString());
        beanWrapper.setPropertyValue(fieldPath, date);
    }
}
```

### Step 2: Register in GenericUpdateService

```java
public GenericUpdateService(SimpleFieldUpdateStrategy simpleStrategy,
                            NestedFieldUpdateStrategy nestedStrategy,
                            ListElementUpdateStrategy listStrategy,
                            DateFieldUpdateStrategy dateStrategy) {
    this.listStrategy = listStrategy;
    strategies.add(dateStrategy);        // Add custom strategy
    strategies.add(listStrategy);
    strategies.add(nestedStrategy);
    strategies.add(simpleStrategy);
}
```

## Error Handling

Each strategy handles its own errors:
- Logs warnings for invalid paths
- Logs errors for exceptions
- Continues processing remaining updates

## Logging

Each strategy logs at appropriate levels:
- **DEBUG**: Successful updates
- **WARN**: Invalid paths or null objects
- **ERROR**: Exceptions during update

## Example Scenarios

### Scenario 1: Simple Field Update
```json
{
  "name": "Updated Product"
}
```
→ Uses `SimpleFieldUpdateStrategy`

### Scenario 2: Nested Object Update
```json
{
  "metadata.version": "2.0",
  "metadata.author": "John Doe"
}
```
→ Uses `NestedFieldUpdateStrategy`

### Scenario 3: List Element Update
```json
{
  "productDetails.spec-A.logistics.shippingWeightKg": 5.5,
  "productDetails.spec-B.description": "Updated description"
}
```
→ Uses `ListElementUpdateStrategy`

### Scenario 4: Mixed Updates
```json
{
  "name": "Updated Product",
  "metadata.version": "2.0",
  "productDetails.spec-A.logistics.shippingWeightKg": 5.5
}
```
→ Uses all three strategies

## Testing

### Unit Test Example

```java
@Test
void testStrategyPattern() {
    SimpleFieldUpdateStrategy simpleStrategy = new SimpleFieldUpdateStrategy();
    NestedFieldUpdateStrategy nestedStrategy = new NestedFieldUpdateStrategy();
    ListElementUpdateStrategy listStrategy = new ListElementUpdateStrategy();
    
    listStrategy.configure("productDetails", "detailId");
    
    GenericUpdateService<Product> service = new GenericUpdateService<>(
        simpleStrategy, nestedStrategy, listStrategy
    );
    
    Product product = createTestProduct();
    Map<String, Object> updates = Map.of("name", "Test");
    
    Product result = service.applyUpdates(product, updates);
    
    assertEquals("Test", result.getName());
}
```

## Performance

- **O(n × m)** where n = number of updates, m = number of strategies
- Strategies checked in order until match found
- Most specific strategies first for early exit

## Best Practices

1. **Order matters**: Place most specific strategies first
2. **Fail fast**: Return early from canHandle() when possible
3. **Log appropriately**: Use correct log levels
4. **Handle nulls**: Check for null objects before updating
5. **Test independently**: Unit test each strategy separately

## Migration from Old Code

### Before (Monolithic)
```java
if (fieldPath.startsWith(listPathPrefix)) {
    // 50+ lines of list handling
} else if (fieldPath.contains(".")) {
    // 30+ lines of nested handling
} else {
    // 10+ lines of simple handling
}
```

### After (Strategy Pattern)
```java
for (UpdateStrategy strategy : strategies) {
    if (strategy.canHandle(fieldPath)) {
        strategy.applyUpdate(beanWrapper, fieldPath, value);
        break;
    }
}
```

## Summary

✅ **Cleaner code** - Each strategy is focused and simple  
✅ **Extensible** - Add new strategies without modifying existing code  
✅ **Testable** - Unit test each strategy independently  
✅ **Maintainable** - Easy to understand and debug  
✅ **Production-ready** - Proper error handling and logging
