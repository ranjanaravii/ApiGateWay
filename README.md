# API Gateway using Spring Cloud Gateway

A comprehensive API Gateway implementation using Spring Cloud Gateway with advanced features including authentication, rate limiting, circuit breakers, and service discovery.

## Features

- **Service Discovery**: Integration with Netflix Eureka for automatic service registration and discovery
- **Load Balancing**: Automatic load balancing across service instances
- **Circuit Breaker**: Resilience4j integration for fault tolerance
- **Rate Limiting**: Redis-based rate limiting with multiple strategies
- **Authentication**: JWT token validation and user context propagation
- **Logging**: Comprehensive request/response logging with correlation IDs
- **CORS**: Configurable Cross-Origin Resource Sharing
- **Health Checks**: Built-in health monitoring and service status
- **Fallback Handling**: Graceful degradation when services are unavailable
- **Security**: Spring Security integration with customizable rules

## Architecture

```
Client Request → API Gateway → Service Discovery → Microservice
                     ↓
              [Filters Applied]
              - Authentication
              - Rate Limiting
              - Logging
              - Circuit Breaker
```

## Prerequisites

- Java 21+
- Maven 3.6+
- Redis Server (for rate limiting)
- Eureka Server (for service discovery)

## Configuration

### Application Properties

The gateway is configured via `application.yml`:

- **Server Port**: 8080
- **Eureka Server**: http://localhost:8761/eureka/
- **Redis**: localhost:6379 (default)

### Route Configuration

Routes are configured in two ways:

1. **Declarative** (application.yml):
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/api/users/**
```

2. **Programmatic** (GatewayConfig.java):
```java
.route("auth-service", r -> r
    .path("/api/auth/**")
    .filters(f -> f.stripPrefix(2))
    .uri("lb://auth-service"))
```

## API Endpoints

### Health Endpoints
- `GET /api/health` - Gateway health status
- `GET /api/health/services` - Discovered services health
- `GET /actuator/health` - Spring Boot actuator health

### Service Routes
- `/api/users/**` → user-service
- `/api/products/**` → product-service
- `/api/orders/**` → order-service
- `/api/auth/**` → auth-service
- `/api/notifications/**` → notification-service
- `/api/files/**` → file-service
- `/api/admin/**` → admin-service

### Fallback Endpoints
- `/fallback/users` - User service fallback
- `/fallback/products` - Product service fallback
- `/fallback/orders` - Order service fallback
- `/fallback/auth` - Auth service fallback

## Security

### Authentication
- JWT token validation via `Authorization: Bearer <token>` header
- Public endpoints: `/api/auth/login`, `/api/auth/register`, `/api/health`
- User context propagation via `X-User-Id` and `X-User-Role` headers

### CORS
- Configurable origins, methods, and headers
- Credentials support
- Pre-flight request handling

## Rate Limiting

Multiple rate limiting strategies:

1. **User-based**: By `X-User-Id` header
2. **IP-based**: By client IP address
3. **API Key-based**: By `X-API-Key` header

Default limits:
- 10 requests per second (replenish rate)
- 20 requests burst capacity

## Circuit Breaker

Resilience4j configuration:
- **Sliding window**: 10 requests
- **Failure threshold**: 50%
- **Wait duration**: 5 seconds
- **Half-open calls**: 3

## Monitoring

### Metrics
- Prometheus metrics via `/actuator/prometheus`
- Custom gateway metrics
- JVM and system metrics

### Logging
- Request/response correlation IDs
- Performance timing
- Error tracking
- Service discovery events

## Running the Application

### 1. Start Dependencies
```bash
# Start Redis
redis-server

# Start Eureka Server (separate application)
# Default port: 8761
```

### 2. Build and Run
```bash
# Build the application
mvn clean compile

# Run the application
mvn spring-boot:run

# Or run the JAR
mvn clean package
java -jar target/ApiGateWay-0.0.1-SNAPSHOT.jar
```

### 3. Verify Setup
```bash
# Check gateway health
curl http://localhost:8080/api/health

# Check discovered services
curl http://localhost:8080/api/health/services

# Check actuator endpoints
curl http://localhost:8080/actuator/health
```

## Testing

### Unit Tests
```bash
mvn test
```

### Integration Testing
```bash
# Test with a sample service call
curl -H "Authorization: Bearer sample-token" \
     http://localhost:8080/api/users/profile

# Test rate limiting
for i in {1..25}; do
  curl http://localhost:8080/api/health
done
```

## Development

### Adding New Routes
1. Update `application.yml` for simple routes
2. Modify `GatewayConfig.java` for complex routing logic
3. Add fallback handlers in `FallbackController.java`

### Custom Filters
1. Extend `AbstractGatewayFilterFactory`
2. Implement filter logic
3. Register as Spring component

### Environment Configuration
```bash
# Development
export SPRING_PROFILES_ACTIVE=dev

# Production
export SPRING_PROFILES_ACTIVE=prod
export EUREKA_SERVER_URL=http://eureka-server:8761/eureka/
export REDIS_HOST=redis-server
```

## Troubleshooting

### Common Issues

1. **Service Discovery Issues**
   - Verify Eureka server is running
   - Check network connectivity
   - Review service registration logs

2. **Rate Limiting Not Working**
   - Ensure Redis is running and accessible
   - Check Redis connection configuration
   - Verify rate limit keys are being generated

3. **Circuit Breaker Not Triggering**
   - Check failure rate threshold
   - Verify minimum number of calls
   - Review circuit breaker metrics

### Logs Location
- Application logs: `logs/api-gateway.log`
- Console output for development

## Production Considerations

1. **Security**
   - Use HTTPS in production
   - Implement proper JWT validation
   - Configure CORS for specific origins
   - Enable security headers

2. **Performance**
   - Configure connection pools
   - Set appropriate timeouts
   - Monitor memory usage
   - Use Redis cluster for rate limiting

3. **Monitoring**
   - Set up Prometheus + Grafana
   - Configure alerting
   - Monitor circuit breaker states
   - Track response times

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the MIT License.
