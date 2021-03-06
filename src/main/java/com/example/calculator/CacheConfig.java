package com.example.calculator;

import com.example.calculator.model.OperationResult;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Cache configuration
 */
@Configuration
public class CacheConfig {

    /**
     * Defines the cached used to store the operation results.
     */
    @Bean
    public Cache<String, OperationResult> resultsCache() {
        return CacheBuilder.newBuilder()
                .maximumSize(2000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();
    }
}
