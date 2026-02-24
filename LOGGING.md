# Logging Configuration

## Overview

The application uses **SLF4J** with **Logback** for structured logging.

## Log Levels

| Level | Usage | Example |
|-------|-------|---------|
| **ERROR** | System errors, exceptions | Database connection failures |
| **WARN** | Warning conditions | Product not found, invalid updates |
| **INFO** | Important business events | Product created, deleted, updated |
| **DEBUG** | Detailed diagnostic info | Method entry/exit, query details |
| **TRACE** | Very detailed info | Not used in production |

## Log Files

### Location: `logs/` directory

| File | Content | Rotation |
|------|---------|----------|
| `application.log` | All application logs | Daily, max 10MB per file |
| `error.log` | ERROR level only | Daily |
| `application-{date}.{index}.log` | Archived logs | Keep 30 days |

## Configuration

### application.properties

```properties
# Logging Configuration
logging.level.root=INFO
logging.level.com.example.product=DEBUG
logging.level.org.springframework.data.mongodb.core=INFO
logging.level.org.springframework.web=INFO
```

### logback-spring.xml

Advanced configuration with:
- Console output
- File rotation (daily, 10MB max)
- Separate error log
- 30-day retention

## Log Examples

### Product Created
```
2024-02-22 20:45:30.123 [http-nio-7070-exec-1] INFO  c.e.p.service.ProductService - Product saved successfully with ID: 65abc123def456789
```

### Product Not Found
```
2024-02-22 20:45:35.456 [http-nio-7070-exec-2] WARN  c.e.p.service.ProductService - Product not found with ID: invalid-id
```

### Update Error
```
2024-02-22 20:45:40.789 [http-nio-7070-exec-3] ERROR c.e.p.service.GenericUpdateService - Error applying update for path 'productDetails.spec-1.color': Field not writable
```

### Debug Information
```
2024-02-22 20:45:45.012 [http-nio-7070-exec-4] DEBUG c.e.p.service.ProductService - Fetching product by ID: 65abc123def456789
2024-02-22 20:45:45.034 [http-nio-7070-exec-4] DEBUG c.e.p.service.ProductService - Updating fields for product ID: 65abc123def456789 with 3 updates
```

## Logger Usage in Code

### Service Layer

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ProductService {
    
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    
    public ProductResponseDto saveProduct(ProductRequestDto dto) {
        log.debug("Saving new product: {}", dto.getName());
        // ... business logic
        log.info("Product saved successfully with ID: {}", savedProduct.getId());
        return response;
    }
    
    public void deleteProduct(String id) {
        log.debug("Deleting product with ID: {}", id);
        if (!exists(id)) {
            log.warn("Cannot delete - Product not found with ID: {}", id);
            throw new ProductNotFoundException(id);
        }
        // ... delete logic
        log.info("Product deleted successfully: {}", id);
    }
}
```

### Exception Handling

```java
try {
    // risky operation
} catch (Exception e) {
    log.error("Error processing product: {}", productId, e);
    throw new ProcessingException("Failed to process", e);
}
```

## Production Configuration

### Reduce Log Verbosity

```properties
logging.level.root=WARN
logging.level.com.example.product=INFO
logging.level.org.springframework=WARN
```

### Increase File Retention

```xml
<maxHistory>90</maxHistory>  <!-- Keep 90 days -->
```

### Add JSON Logging (for log aggregation)

```xml
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>7.4</version>
</dependency>
```

## Best Practices

✅ **Use parameterized logging**
```java
log.info("Product {} created by user {}", productId, userId);  // Good
log.info("Product " + productId + " created");  // Bad (string concatenation)
```

✅ **Log at appropriate levels**
- ERROR: Exceptions, system failures
- WARN: Recoverable issues, missing data
- INFO: Business events, state changes
- DEBUG: Diagnostic information

✅ **Include context**
```java
log.info("Product saved successfully with ID: {}", savedProduct.getId());
```

✅ **Log exceptions with stack traces**
```java
log.error("Failed to save product: {}", productId, exception);
```

❌ **Avoid**
- Logging sensitive data (passwords, tokens)
- Excessive DEBUG logs in production
- System.out.println() or System.err.println()
- Logging in loops (use batch logging)

## Monitoring Integration

### ELK Stack (Elasticsearch, Logstash, Kibana)
- Use JSON format
- Add correlation IDs
- Include timestamps

### Splunk
- Forward logs to Splunk forwarder
- Use structured logging

### CloudWatch (AWS)
- Use CloudWatch agent
- Configure log groups

## Log Rotation

Automatic rotation configured:
- **Size-based**: 10MB per file
- **Time-based**: Daily rotation
- **Retention**: 30 days
- **Compression**: Gzip archived files

## Troubleshooting

### Logs not appearing
1. Check log level configuration
2. Verify logback-spring.xml is in classpath
3. Check file permissions for logs/ directory

### Too many log files
1. Reduce maxHistory in logback-spring.xml
2. Increase maxFileSize to reduce file count
3. Add cleanup script

### Performance issues
1. Reduce log level in production
2. Use async appenders
3. Disable DEBUG logging

## Async Logging (Optional)

For high-throughput applications:

```xml
<appender name="ASYNC" class="ch.qos.logback.classic.AsyncAppender">
    <appender-ref ref="FILE"/>
    <queueSize>512</queueSize>
    <discardingThreshold>0</discardingThreshold>
</appender>
```

## Summary

✅ **SLF4J + Logback** - Industry standard  
✅ **Structured logging** - Easy to parse and analyze  
✅ **File rotation** - Automatic cleanup  
✅ **Multiple appenders** - Console + File + Error file  
✅ **Production-ready** - Configurable for different environments  
