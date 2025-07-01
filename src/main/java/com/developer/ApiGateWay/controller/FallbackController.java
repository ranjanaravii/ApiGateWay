package com.developer.ApiGateWay.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/users")
    @PostMapping("/users")
    public ResponseEntity<Map<String, Object>> userServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User service is currently unavailable. Please try again later.");
        response.put("service", "user-service");
        response.put("status", "fallback");
        response.put("timestamp", LocalDateTime.now());
        response.put("suggestion", "Check user service health or contact support");
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/products")
    @PostMapping("/products")
    public ResponseEntity<Map<String, Object>> productServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Product service is currently unavailable. Please try again later.");
        response.put("service", "product-service");
        response.put("status", "fallback");
        response.put("timestamp", LocalDateTime.now());
        response.put("suggestion", "Browse cached products or try again in a few minutes");
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/orders")
    @PostMapping("/orders")
    public ResponseEntity<Map<String, Object>> orderServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Order service is currently unavailable. Please try again later.");
        response.put("service", "order-service");
        response.put("status", "fallback");
        response.put("timestamp", LocalDateTime.now());
        response.put("suggestion", "Your order may be saved. Please check order history later");
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/auth")
    @PostMapping("/auth")
    public ResponseEntity<Map<String, Object>> authServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Authentication service is currently unavailable. Please try again later.");
        response.put("service", "auth-service");
        response.put("status", "fallback");
        response.put("timestamp", LocalDateTime.now());
        response.put("suggestion", "Try logging in again or contact support if issue persists");
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/default")
    @PostMapping("/default")
    public ResponseEntity<Map<String, Object>> defaultFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Service is currently unavailable. Please try again later.");
        response.put("service", "unknown");
        response.put("status", "fallback");
        response.put("timestamp", LocalDateTime.now());
        response.put("suggestion", "Please try again in a few minutes or contact support");
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}
