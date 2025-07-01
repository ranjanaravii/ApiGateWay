package com.developer.ApiGateWay.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimitConfig {

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            // Rate limit by user ID if available, otherwise by IP
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            if (userId != null) {
                return Mono.just(userId);
            }
            
            // Fallback to IP-based rate limiting
            String clientIp = exchange.getRequest().getRemoteAddress() != null 
                ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                : "unknown";
            
            return Mono.just(clientIp);
        };
    }

    @Bean
    public KeyResolver apiKeyResolver() {
        return exchange -> {
            // Rate limit by API key if available
            String apiKey = exchange.getRequest().getHeaders().getFirst("X-API-Key");
            if (apiKey != null) {
                return Mono.just(apiKey);
            }
            
            // Fallback to path-based rate limiting
            return Mono.just(exchange.getRequest().getPath().value());
        };
    }
}
