package com.developer.ApiGateWay.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Order(-1)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        // Log the exception
        logger.error("Gateway Exception: {} - Path: {}", ex.getMessage(), exchange.getRequest().getPath(), ex);

        // Set response headers
        response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        
        HttpStatus status;
        String message;
        
        if (ex instanceof ResponseStatusException) {
            ResponseStatusException rse = (ResponseStatusException) ex;
            status = HttpStatus.valueOf(rse.getStatusCode().value());
            message = rse.getReason();
        } else if (ex instanceof java.net.ConnectException) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            message = "Service temporarily unavailable";
        } else if (ex instanceof java.util.concurrent.TimeoutException) {
            status = HttpStatus.REQUEST_TIMEOUT;
            message = "Request timeout";
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "Internal server error";
        }
        
        response.setStatusCode(status);
        
        String body = String.format(
            "{\"error\": \"%s\", \"status\": %d, \"timestamp\": \"%s\", \"path\": \"%s\", \"requestId\": \"%s\"}",
            message,
            status.value(),
            LocalDateTime.now().format(formatter),
            exchange.getRequest().getPath().value(),
            exchange.getRequest().getHeaders().getFirst("X-Request-ID")
        );
        
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes());
        return response.writeWith(Mono.just(buffer));
    }
}
