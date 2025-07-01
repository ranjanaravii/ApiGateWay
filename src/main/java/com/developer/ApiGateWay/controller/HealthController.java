package com.developer.ApiGateWay.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController implements HealthIndicator {

    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "api-gateway");
        health.put("version", "1.0.0");
        
        // Get registered services
        List<String> services = discoveryClient.getServices();
        health.put("discoveredServices", services);
        health.put("serviceCount", services.size());
        
        // Add system info
        Map<String, Object> system = new HashMap<>();
        system.put("javaVersion", System.getProperty("java.version"));
        system.put("osName", System.getProperty("os.name"));
        system.put("osVersion", System.getProperty("os.version"));
        system.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        system.put("maxMemory", Runtime.getRuntime().maxMemory());
        system.put("totalMemory", Runtime.getRuntime().totalMemory());
        system.put("freeMemory", Runtime.getRuntime().freeMemory());
        
        health.put("system", system);
        
        return ResponseEntity.ok(health);
    }

    @GetMapping("/services")
    public ResponseEntity<Map<String, Object>> getServicesHealth() {
        Map<String, Object> servicesHealth = new HashMap<>();
        List<String> services = discoveryClient.getServices();
        
        for (String service : services) {
            Map<String, Object> serviceInfo = new HashMap<>();
            serviceInfo.put("instances", discoveryClient.getInstances(service).size());
            serviceInfo.put("status", "DISCOVERED");
            servicesHealth.put(service, serviceInfo);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("totalServices", services.size());
        response.put("services", servicesHealth);
        
        return ResponseEntity.ok(response);
    }

    @Override
    public Health health() {
        try {
            List<String> services = discoveryClient.getServices();
            return Health.up()
                    .withDetail("discoveredServices", services.size())
                    .withDetail("services", services)
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
