# Security Center - Configuration Guide

## Quick Start

### Minimum Configuration for Keycloak

```yaml
firefly:
  security-center:
    idp:
      provider: keycloak
      keycloak:
        server-url: http://localhost:8080
        realm: your-realm
        client-id: your-client
        client-secret: your-secret
        admin-username: admin
        admin-password: admin
```

### Minimum Configuration for AWS Cognito

```yaml
firefly:
  security-center:
    idp:
      provider: cognito
      cognito:
        region: us-east-1
        user-pool-id: us-east-1_XXXXXX
        client-id: your-client-id
```

## Identity Provider Configuration

### Keycloak

Full Keycloak configuration with all available properties:

```yaml
firefly:
  security-center:
    idp:
      provider: keycloak                          # Options: keycloak, cognito
      keycloak:
        server-url: http://localhost:8080         # Required
        realm: your-realm                         # Required
        client-id: your-client                    # Required
        client-secret: your-secret                # Required for confidential clients
        admin-username: admin                     # Required for admin operations
        admin-password: admin                     # Required for admin operations
  connection-pool-size: 10                        # Optional, default: 10
  connection-timeout: 30000                       # Optional, default: 30000ms
  request-timeout: 60000                          # Optional, default: 60000ms
```

**Notes:**
- Use confidential clients in production with `client-secret`
- Public clients don't require `client-secret`
- Adjust timeouts based on network latency
- Connection pool size should match expected concurrent load

### AWS Cognito

Full AWS Cognito configuration:

```yaml
firefly:
  security-center:
    idp:
      provider: cognito
      cognito:
        region: us-east-1                         # Required: AWS region
        user-pool-id: us-east-1_XXXXXX           # Required: Cognito User Pool ID
        client-id: your-client-id                 # Required: App client ID
        client-secret: your-client-secret         # Optional: For confidential clients
        endpoint-override: http://localhost:4566  # Optional: For LocalStack testing
```

**Notes:**
- `endpoint-override` only for testing with LocalStack
- Requires AWS credentials configured (IAM role, env vars, or AWS CLI)
- Client secret required only for confidential app clients
- Recommended to use IAM roles in production (ECS, EC2, Lambda)

## Cache Configuration

### Redis (Recommended for Production)

```yaml
firefly:
  cache:
    default-cache-type: REDIS
    redis:
      enabled: true
      host: localhost                             # Redis server host
      port: 6379                                  # Redis server port
      database: 0                                 # Redis database number
      password: your-redis-password               # Optional: Redis password
      key-prefix: "firefly:session"               # Key prefix for sessions
      timeout: 5000                               # Connection timeout (ms)
      pool:
        max-active: 8                             # Max connections
        max-idle: 8                               # Max idle connections
        min-idle: 2                               # Min idle connections
        max-wait: -1                              # Max wait for connection (ms)
      ssl:
        enabled: false                            # Enable TLS
```

**Production Recommendations:**
- Enable SSL/TLS in production
- Use strong password
- Configure connection pooling based on load
- Use Redis Cluster or Sentinel for high availability
- Monitor connection pool metrics

### Caffeine (Development/Testing)

```yaml
firefly:
  cache:
    default-cache-type: CAFFEINE
    caffeine:
      enabled: true
      spec: "maximumSize=1000,expireAfterWrite=3600s"
```

**Notes:**
- In-memory cache, not shared across instances
- Good for development and testing
- Automatic eviction based on spec
- No external dependencies

## Downstream Service Configuration

Configure URLs for dependent microservices:

```yaml
firefly:
  security-center:
    clients:
      customer-mgmt:
        base-url: ${CUSTOMER_MGMT_URL:http://localhost:8081}
        timeout: 5000
        max-retries: 3
      contract-mgmt:
        base-url: ${CONTRACT_MGMT_URL:http://localhost:8082}
        timeout: 5000
        max-retries: 3
      product-mgmt:
        base-url: ${PRODUCT_MGMT_URL:http://localhost:8083}
        timeout: 5000
        max-retries: 3
      reference-master-data:
        base-url: ${REFERENCE_DATA_URL:http://localhost:8084}
        timeout: 5000
        max-retries: 3
```

**Notes:**
- Use environment variables for URLs
- Configure timeouts based on expected response times
- Implement circuit breaker for resilience (future enhancement)

## Session Configuration

```yaml
firefly:
  security-center:
    session:
      default-ttl: 3600                           # Default TTL in seconds
      max-ttl: 86400                              # Maximum TTL in seconds
      enable-sliding-expiration: true             # Extend TTL on access
```

## Application Server Configuration

### Port and Context

```yaml
server:
  port: 8080
  servlet:
    context-path: /security-center
```

### WebFlux (Reactive)

```yaml
spring:
  webflux:
    base-path: /api/v1
```

## Logging Configuration

```yaml
logging:
  level:
    io.fireflysoftware.securitycenter: INFO
    io.fireflysoftware.securitycenter.core: DEBUG        # Core business logic
    io.fireflysoftware.securitycenter.web: INFO          # REST controllers
    org.springframework.security: DEBUG           # Spring Security
    org.springframework.cache: DEBUG              # Cache operations
    io.lettuce.core: WARN                         # Redis client
```

## Actuator and Monitoring

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized
  metrics:
    export:
      prometheus:
        enabled: true
```

## Profile-Specific Configuration

### Development Profile (`application-dev.yml`)

```yaml
spring:
  config:
    activate:
      on-profile: dev

firefly:
  security-center:
    idp:
      provider: keycloak
  cache:
    default-cache-type: CAFFEINE

  security-center:
    idp:
      provider: keycloak
      keycloak:
        server-url: http://localhost:8080
        realm: firefly-dev
        client-id: security-center-dev
        client-secret: dev-secret
        admin-username: admin
        admin-password: admin

logging:
  level:
    io.fireflysoftware: DEBUG
```

### Production Profile (`application-prod.yml`)

```yaml
spring:
  config:
    activate:
      on-profile: prod

firefly:
  security-center:
    idp:
      provider: cognito
      cognito:
        region: ${AWS_REGION}
        user-pool-id: ${COGNITO_USER_POOL_ID}
        client-id: ${COGNITO_CLIENT_ID}
  cache:
    default-cache-type: REDIS
    redis:
      enabled: true
      host: ${REDIS_HOST}
      port: ${REDIS_PORT}
      password: ${REDIS_PASSWORD}
      ssl:
        enabled: true

logging:
  level:
    io.fireflysoftware: INFO
```

### Test Profile (`application-test.yml`)

```yaml
spring:
  config:
    activate:
      on-profile: test

firefly:
  cache:
    default-cache-type: CAFFEINE

# Testcontainers will override IDP settings
```

## Environment Variables

Use environment variables for sensitive configuration:

```bash
# AWS Cognito
export AWS_REGION=us-east-1
export COGNITO_USER_POOL_ID=us-east-1_XXXXXX
export COGNITO_CLIENT_ID=your-client-id
export COGNITO_CLIENT_SECRET=your-secret

# Keycloak
export KEYCLOAK_SERVER_URL=https://keycloak.example.com
export KEYCLOAK_REALM=production
export KEYCLOAK_CLIENT_ID=security-center
export KEYCLOAK_CLIENT_SECRET=your-secret

# Redis
export REDIS_HOST=redis.example.com
export REDIS_PORT=6379
export REDIS_PASSWORD=your-redis-password

# Downstream services
export CUSTOMER_MGMT_URL=https://customer-mgmt.example.com
export CONTRACT_MGMT_URL=https://contract-mgmt.example.com
export PRODUCT_MGMT_URL=https://product-mgmt.example.com
export REFERENCE_DATA_URL=https://reference-data.example.com

# Testing (LocalStack)
export LOCALSTACK_AUTH_TOKEN=your-localstack-token
```

## Docker Compose Example

```yaml
version: '3.8'

services:
  security-center:
    image: firefly/security-center:latest
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      FIREFLY_SECURITY_CENTER_IDP_PROVIDER: cognito
      AWS_REGION: us-east-1
      COGNITO_USER_POOL_ID: us-east-1_XXXXXX
      COGNITO_CLIENT_ID: your-client-id
      REDIS_HOST: redis
      REDIS_PORT: 6379
    depends_on:
      - redis

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    command: redis-server --requirepass your-redis-password
```

## Kubernetes ConfigMap Example

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: security-center-config
data:
  application.yml: |
    firefly:
      security-center:
        idp:
          provider: cognito
      cache:
        default-cache-type: REDIS
        redis:
          enabled: true
          host: redis-service
          port: 6379
          ssl:
            enabled: true
```

## AWS Systems Manager Parameter Store

For production secrets, use AWS Systems Manager:

```bash
# Store secrets
aws ssm put-parameter \
  --name /firefly/security-center/cognito/client-secret \
  --value "your-secret" \
  --type SecureString

aws ssm put-parameter \
  --name /firefly/security-center/redis/password \
  --value "your-redis-password" \
  --type SecureString

# Reference in Spring Boot
spring:
  config:
    import: aws-parameterstore:/firefly/security-center/
```

## Configuration Validation

The application validates configuration at startup:

```java
// Fails fast if required properties are missing
@ConfigurationProperties(prefix = "firefly.security-center.idp.cognito")
@Validated
public class CognitoProperties {
    @NotBlank
    private String region;
    
    @NotBlank
    private String userPoolId;
    
    @NotBlank
    private String clientId;
}
```

## Configuration Best Practices

1. **Use Environment Variables** for sensitive data
2. **Use Profiles** for environment-specific config
3. **Externalize Configuration** using ConfigMaps or Parameter Store
4. **Validate Early** with `@Validated` annotations
5. **Set Reasonable Defaults** for optional properties
6. **Document Required Properties** clearly
7. **Use Strong Types** for configuration classes
8. **Enable TLS/SSL** in production
9. **Rotate Secrets** regularly
10. **Monitor Configuration Changes** with audit logs
