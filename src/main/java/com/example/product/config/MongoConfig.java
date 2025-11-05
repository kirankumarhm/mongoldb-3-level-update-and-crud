package com.example.product.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * MongoDB configuration class.
 * Can be used for more advanced MongoDB configurations,
 * such as custom converters or auditing.
 */
@Configuration // Marks this class as a Spring configuration class
@EnableMongoAuditing // Enables auditing features like @CreatedDate, @LastModifiedDate etc.
public class MongoConfig {
    // You can add custom converters here if needed, for example:
    // @Bean
    // public MongoCustomConversions customConversions() {
    //     return new MongoCustomConversions(Arrays.asList(new MyCustomConverter()));
    // }
}
