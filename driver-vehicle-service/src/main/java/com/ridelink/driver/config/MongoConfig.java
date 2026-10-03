package com.ridelink.driver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * Enables MongoDB auditing so that {@code @CreatedDate} and {@code @LastModifiedDate}
 * annotations on domain models are automatically populated.
 */
@Configuration
@EnableMongoAuditing
public class MongoConfig {
}
