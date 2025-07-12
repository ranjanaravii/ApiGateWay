package com.developer.ApiGateWay.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Authentication Service Route
                .route("auth-service", r -> r
                        .path("/api/auth/**")
                        .filters(f -> f
                                .stripPrefix(2)
                                .addRequestHeader("X-Gateway", "API-Gateway")
                                .addResponseHeader("X-Response-Time", String.valueOf(System.currentTimeMillis())))
                        .uri("lb://auth-service"))
                
                // Notification Service Route
                .route("notification-service", r -> r
                        .path("/api/notifications/**")
                        
                        .filters(f -> f
                                .stripPrefix(2)
                                .addRequestHeader("X-Gateway", "API-Gateway"))
                        .uri("lb://notification-service"))
                
                // File Service Route with size limit
                .route("file-service", r -> r
                        .path("/api/files/**")
                        .and()
                        .header("Content-Length", "\\d+")
                        .filters(f -> f
                                .stripPrefix(2)) 
                        .uri("lb://file-service"))
                
                // Admin routes with specific method restrictions
                .route("admin-service", r -> r
                        .path("/api/admin/**")
                        .and()
                        .method("GET", "POST", "PUT", "DELETE")
                        .filters(f -> f
                                .stripPrefix(2)
                                .addRequestHeader("X-Admin-Request", "true"))
                        .uri("lb://admin-service"))
                
                .build();
    }
}
