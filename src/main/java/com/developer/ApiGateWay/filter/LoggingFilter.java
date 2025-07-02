package com.developer.ApiGateWay.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String requestId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();
        
        // Log incoming request
        logger.info("Incoming Request [{}] - Method: {}, URI: {}, Headers: {}, Remote Address: {}, Timestamp: {}",
                requestId,
                request.getMethod(),
                request.getURI(),
                request.getHeaders().toSingleValueMap(),
                request.getRemoteAddress(),
                LocalDateTime.now().format(formatter));
        
        // Add request ID to headers for tracing
        ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-Request-ID", requestId)
                .header("X-Request-Timestamp", String.valueOf(startTime))
                .build();
        
        return chain.filter(exchange.mutate().request(modifiedRequest).build())
                .doFinally(signalType -> {
                    ServerHttpResponse response = exchange.getResponse();
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;
                    
                    // Log outgoing response
                    logger.info("Outgoing Response [{}] - Status: {}, Duration: {}ms, Headers: {}, Timestamp: {}",
                            requestId,
                            response.getStatusCode(),
                            duration,
                            response.getHeaders().toSingleValueMap(),
                            LocalDateTime.now().format(formatter));
                    
                    // Add response headers
                    response.getHeaders().add("X-Request-ID", requestId);
                    response.getHeaders().add("X-Response-Time", String.valueOf(duration));
                });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
