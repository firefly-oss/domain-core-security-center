# Getting Started with Firefly Security Center

This comprehensive guide will help you set up, configure, and run the Security Center microservice in various environments.

---

## Table of Contents

1. [Understanding the Security Center](#understanding-the-security-center)
2. [Installation](#installation)
3. [Local Development Setup](#local-development-setup)
4. [Production Deployment](#production-deployment)
5. [Integration with Other Services](#integration-with-other-services)
6. [Testing](#testing)
7. [Troubleshooting](#troubleshooting)

---

## Understanding the Security Center

### What Problem Does It Solve?

In a microservices architecture, **authentication and authorization** become complex:

- Each service authenticating users independently (duplicated logic)
- Inconsistent session management across services
- Repeated calls to identity providers (performance bottleneck)
- No centralized view of user permissions

**The Security Center solves this by:**

- Centralizing authentication with pluggable IDP adapters
- Enriching sessions with customer, contract, and product data
- Caching sessions for fast access across all services
- Providing a shared library (`FireflySessionManager`) for session access

### Core Responsibilities

1. **Authentication** - Validate credentials via Keycloak or AWS Cognito
2. **Session Enrichment** - Aggregate data from 4+ microservices
3. **Caching** - Store sessions in Redis/Caffeine for fast retrieval
4. **Authorization Context** - Provide contract-based permissions to all services

---

## Installation

### System Requirements

- **Java**: 25 or higher (OpenJDK recommended)
- **Maven**: 3.8 or higher
- **Memory**: Minimum 2GB RAM for running the service
- **Disk**: 500MB for dependencies and build artifacts

### Clone the Repository

```bash
git clone https://github.com/firefly-oss/domain-core-security-center.git
cd domain-core-security-center
```

### Build the Project

```bash
# Full build with tests
mvn clean install

# Skip tests (faster)
mvn clean install -DskipTests

# Build specific module
mvn clean install -pl domain-core-security-center-web
```

**Expected Output:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: ~55 s
```
Note: Some integration tests (Keycloak, Redis, Cognito) are @Disabled by default and require Docker or LocalStack Pro.

---

## Local Development Setup

### Option 1: Docker Compose (Recommended)

The easiest way to run everything locally.

#### Step 1: Create `docker-compose.yml`

```yaml
version: '3.8'

services:
  keycloak:
    image: quay.io/keycloak/keycloak:23.0
    environment:
      KEYCLOAK_ADMIN: admin
      KEYCLOAK_ADMIN_PASSWORD: admin
    ports:
      - "8080:8080"
    command: start-dev

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    command: redis-server --requirepass firefly123

  security-center:
    build: .
    ports:
      - "8085:8085"
    environment:
      SPRING_PROFILES_ACTIVE: local
      KEYCLOAK_URL: http://keycloak:8080
      KEYCLOAK_REALM: firefly
      KEYCLOAK_CLIENT_ID: security-center
      KEYCLOAK_CLIENT_SECRET: your-secret
      REDIS_HOST: redis
      REDIS_PASSWORD: firefly123
    depends_on:
      - keycloak
      - redis
```

#### Step 2: Create Dockerfile

```dockerfile
FROM eclipse-temurin:25-jdk-alpine
WORKDIR /app
COPY domain-core-security-center-web/target/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### Step 3: Start Everything

```bash
# Build the JAR
mvn clean package -DskipTests

# Start all services
docker-compose up -d

# View logs
docker-compose logs -f security-center
```

#### Step 4: Configure Keycloak

```bash
# Wait for Keycloak to start
sleep 30

# Access Keycloak Admin Console
open http://localhost:8080/admin
# Login: admin / admin

# Create realm "firefly" and client "security-center" (see Quick Start guide)
```

### Option 2: Manual Setup

Run each component separately for more control.

#### Step 1: Start Keycloak

```bash
docker run -d \
  --name keycloak \
  -p 8080:8080 \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin \
  quay.io/keycloak/keycloak:23.0 \
  start-dev
```

#### Step 2: Start Redis (Optional)

```bash
docker run -d \
  --name redis \
  -p 6379:6379 \
  redis:7-alpine \
  redis-server --requirepass firefly123
```

Or use Caffeine cache (no Redis needed):
```yaml
firefly:
  cache:
    default-cache-type: CAFFEINE
```

#### Step 3: Configure Keycloak

1. Open http://localhost:8080/admin
2. Login: admin / admin
3. Create realm: `firefly`
4. Create client: `security-center` (confidential)
5. Copy client secret from Credentials tab
6. Create user: `testuser` / `password123`

#### Step 4: Configure Security Center

Edit `domain-core-security-center-web/src/main/resources/application-local.yml`:

```yaml
server:
  port: 8085

firefly:
  security-center:
    idp:
      provider: keycloak
      keycloak:
        server-url: http://localhost:8080
        realm: firefly
        client-id: security-center
        client-secret: PASTE_YOUR_CLIENT_SECRET_HERE
        admin-username: admin
        admin-password: admin
    
    clients:
      customer-mgmt:
        base-url: http://localhost:8081
      contract-mgmt:
        base-url: http://localhost:8082
      product-mgmt:
        base-url: http://localhost:8083
      reference-master-data:
        base-url: http://localhost:8084

  cache:
    default-cache-type: CAFFEINE  # or REDIS
    caffeine:
      enabled: true
      maximum-size: 10000
      expire-after-write: PT30M

logging:
  level:
    com.firefly.security.center: DEBUG
```

#### Step 5: Run Security Center

```bash
mvn spring-boot:run -pl domain-core-security-center-web -Dspring-boot.run.profiles=local
```

#### Step 6: Test Authentication

```bash
curl -X POST http://localhost:8085/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

**Expected Response:**
```json
{
  "accessToken": "eyJhbGci...",
  "refreshToken": "eyJhbGci...",
  "idToken": "eyJhbGci...",
  "tokenType": "Bearer",
  "expiresIn": 300,
  "sessionId": "session_123e4567-e89b-12d3-a456-426614174000_1698350040000",
  "partyId": "123e4567-e89b-12d3-a456-426614174000"
}
```

---

## Production Deployment

### Prerequisites

- AWS Account (for Cognito and Redis ElastiCache)
- Kubernetes cluster or ECS/Fargate
- Downstream microservices running

### Step 1: Create AWS Resources

#### Cognito User Pool

```bash
aws cognito-idp create-user-pool \
  --pool-name firefly-production \
  --policies "PasswordPolicy={MinimumLength=8,RequireUppercase=true,RequireLowercase=true,RequireNumbers=true,RequireSymbols=true}" \
  --auto-verified-attributes email \
  --mfa-configuration OPTIONAL \
  --user-attribute-update-settings "AttributesRequireVerificationBeforeUpdate=email"

# Note the UserPoolId
```

#### App Client

```bash
aws cognito-idp create-user-pool-client \
  --user-pool-id us-east-1_XXXXXX \
  --client-name security-center-prod \
  --generate-secret \
  --explicit-auth-flows ALLOW_USER_PASSWORD_AUTH ALLOW_REFRESH_TOKEN_AUTH \
  --token-validity-units "AccessToken=minutes,IdToken=minutes,RefreshToken=days" \
  --access-token-validity 60 \
  --id-token-validity 60 \
  --refresh-token-validity 30

# Note the ClientId and ClientSecret
```

#### Redis ElastiCache

```bash
aws elasticache create-replication-group \
  --replication-group-id firefly-sessions \
  --replication-group-description "Session cache for Firefly Security Center" \
  --engine redis \
  --cache-node-type cache.t3.medium \
  --num-cache-clusters 2 \
  --automatic-failover-enabled \
  --at-rest-encryption-enabled \
  --transit-encryption-enabled \
  --auth-token firefly-redis-password-change-me

# Note the PrimaryEndpoint
```

### Step 2: Create Production Configuration

Create `application-prod.yml`:

```yaml
server:
  port: 8085

firefly:
  security-center:
    idp:
      provider: cognito
      cognito:
        region: ${AWS_REGION}
        user-pool-id: ${COGNITO_USER_POOL_ID}
        client-id: ${COGNITO_CLIENT_ID}
        client-secret: ${COGNITO_CLIENT_SECRET}
    
    session:
      timeout-minutes: 60
      cleanup-interval-minutes: 30
    
    clients:
      customer-mgmt:
        base-url: ${CUSTOMER_MGMT_URL}
        timeout-seconds: 10
      contract-mgmt:
        base-url: ${CONTRACT_MGMT_URL}
        timeout-seconds: 10
      product-mgmt:
        base-url: ${PRODUCT_MGMT_URL}
        timeout-seconds: 10
      reference-master-data:
        base-url: ${REFERENCE_MASTER_DATA_URL}
        timeout-seconds: 10

  cache:
    enabled: true
    default-cache-type: REDIS
    redis:
      enabled: true
      host: ${REDIS_HOST}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD}
      ssl: true
      key-prefix: "firefly:session:prod"
      timeout: 5000

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true

logging:
  level:
    com.firefly.security.center: INFO
    org.springframework.web: WARN
```

### Step 3: Deploy to Kubernetes

Create `k8s-deployment.yaml`:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: security-center
  namespace: firefly
spec:
  replicas: 3
  selector:
    matchLabels:
      app: security-center
  template:
    metadata:
      labels:
        app: security-center
    spec:
      containers:
      - name: security-center
        image: firefly/security-center:1.0.0
        ports:
        - containerPort: 8085
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: AWS_REGION
          value: "us-east-1"
        - name: COGNITO_USER_POOL_ID
          valueFrom:
            secretKeyRef:
              name: security-center-secrets
              key: cognito-user-pool-id
        - name: COGNITO_CLIENT_ID
          valueFrom:
            secretKeyRef:
              name: security-center-secrets
              key: cognito-client-id
        - name: COGNITO_CLIENT_SECRET
          valueFrom:
            secretKeyRef:
              name: security-center-secrets
              key: cognito-client-secret
        - name: REDIS_HOST
          value: "firefly-sessions.abc123.ng.0001.use1.cache.amazonaws.com"
        - name: REDIS_PASSWORD
          valueFrom:
            secretKeyRef:
              name: security-center-secrets
              key: redis-password
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8085
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8085
          initialDelaySeconds: 20
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: security-center
  namespace: firefly
spec:
  selector:
    app: security-center
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8085
  type: LoadBalancer
```

Deploy:
```bash
kubectl apply -f k8s-deployment.yaml
```

---

## Integration with Other Services

### Using FireflySessionManager in Your Microservice

#### Step 1: Add Dependency

Add to your service's `pom.xml`:

```xml
<dependency>
    <groupId>com.firefly</groupId>
    <artifactId>domain-core-security-center-session</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

#### Step 2: Configure Cache Connection

In your service's `application.yml`:

```yaml
firefly:
  cache:
    default-cache-type: REDIS
    redis:
      host: ${REDIS_HOST}
      port: 6379
      password: ${REDIS_PASSWORD}
      key-prefix: "firefly:session"
```

**Important:** Use the **same Redis instance** as the Security Center!

#### Step 3: Inject and Use

```java
@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    @Autowired
    private FireflySessionManager sessionManager;

    @Autowired
    private AccountService accountService;

    @GetMapping("/{accountId}")
    public Mono<AccountDTO> getAccount(
            @PathVariable UUID accountId,
            ServerWebExchange exchange) {

        return sessionManager.createOrGetSession(exchange)
            .flatMap(session -> {
                // 1. Check if user has access to this account
                boolean hasAccess = session.getActiveContracts().stream()
                    .anyMatch(contract ->
                        contract.getProduct().getProductId().equals(accountId));

                if (!hasAccess) {
                    return Mono.error(new UnauthorizedException(
                        "User does not have access to account: " + accountId));
                }

                // 2. Check specific permission (e.g., READ BALANCE)
                boolean canReadBalance = session.getActiveContracts().stream()
                    .filter(c -> c.getProduct().getProductId().equals(accountId))
                    .flatMap(c -> c.getRoleInContract().getScopes().stream())
                    .anyMatch(scope ->
                        "READ".equals(scope.getActionType()) &&
                        "BALANCE".equals(scope.getResourceType()));

                if (!canReadBalance) {
                    return Mono.error(new ForbiddenException(
                        "User does not have permission to read balance"));
                }

                // 3. Fetch account data
                return accountService.getAccountById(accountId);
            });
    }
}
```

#### Step 4: Understanding the Session Object

The `SessionContextDTO` object contains:

```java
public class SessionContextDTO {
    private String sessionId;              // Unique session ID
    private UUID partyId;                  // Customer's party ID
    private CustomerInfoDTO customerInfo;  // Customer profile
    private List<ContractInfoDTO> activeContracts;  // Active contracts
    private LocalDateTime createdAt;       // Session creation time
    private LocalDateTime lastAccessedAt;  // Last access time
    private LocalDateTime expiresAt;       // Session expiration time
    private String ipAddress;              // Client IP address
    private String userAgent;              // User agent string
    private SessionStatus status;          // ACTIVE, EXPIRED, INVALIDATED, LOCKED
    private SessionMetadataDTO metadata;   // Additional metadata
}
```

Each `ContractInfoDTO` contains:

```java
public class ContractInfoDTO {
    private UUID contractId;
    private String contractNumber;
    private String contractStatus;
    private ProductInfoDTO product;        // Product details
    private RoleInfoDTO roleInContract;    // User's role
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private UUID contractPartyId;
    private LocalDateTime dateJoined;
    private LocalDateTime dateLeft;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

Each `RoleInfoDTO` contains:

```java
public class RoleInfoDTO {
    private UUID roleId;
    private String roleCode;
    private String name;                   // e.g., "ACCOUNT_HOLDER", "AUTHORIZED_USER"
    private String description;
    private Boolean isActive;
    private List<RoleScopeInfoDTO> scopes; // Permissions
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
}
```

Each `RoleScopeInfoDTO` contains:

```java
public class RoleScopeInfoDTO {
    private UUID scopeId;
    private UUID roleId;
    private String scopeCode;
    private String scopeName;
    private String description;
    private String actionType;             // e.g., "READ", "WRITE", "DELETE", "EXECUTE", "APPROVE"
    private String resourceType;           // e.g., "PRODUCT", "TRANSACTION", "ACCOUNT", "BALANCE"
    private Boolean isActive;
}
```

---

## Testing

### Running Unit Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=CustomerResolverServiceTest

# Run with coverage
mvn test jacoco:report
```

### Running Integration Tests

```bash
# Keycloak integration test (uses Testcontainers)
mvn test -Dtest=KeycloakIntegrationTest

# Redis cache integration test
mvn test -Dtest=RedisCacheIntegrationTest

# All integration tests
mvn verify
```

### Manual Testing with cURL

#### 1. Login

```bash
curl -X POST http://localhost:8085/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }' | jq
```

Save the `accessToken` and `sessionId` from the response.

#### 2. Get Session Details

```bash
SESSION_ID="session_123e4567-e89b-12d3-a456-426614174000_1698350040000"

curl -X GET "http://localhost:8085/api/v1/sessions/${SESSION_ID}" | jq
```

#### 3. Refresh Token

```bash
curl -X POST http://localhost:8085/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d "{
    \"refreshToken\": \"${REFRESH_TOKEN}\"
  }" | jq
```

#### 4. Logout

```bash
curl -X POST http://localhost:8085/api/v1/auth/logout \
  -H "Content-Type: application/json" \
  -d "{
    \"accessToken\": \"${ACCESS_TOKEN}\",
    \"refreshToken\": \"${REFRESH_TOKEN}\",
    \"sessionId\": \"${SESSION_ID}\"
  }"
```

---

## Troubleshooting

### Common Issues

#### 1. "Connection refused" to Keycloak

**Problem:** Security Center can't connect to Keycloak.

**Solution:**
```bash
# Check if Keycloak is running
curl http://localhost:8080/health/ready

# Check Keycloak logs
docker logs keycloak

# Verify configuration
grep "server-url" application-local.yml
```

#### 2. "Invalid client credentials"

**Problem:** Client secret is incorrect.

**Solution:**
1. Go to Keycloak Admin Console
2. Navigate to Clients → security-center → Credentials
3. Copy the Client Secret
4. Update `application-local.yml`:
   ```yaml
   firefly:
     security-center:
       idp:
         keycloak:
           client-secret: PASTE_CORRECT_SECRET_HERE
   ```

#### 3. "Session not found" errors

**Problem:** Redis cache is not working or sessions are expiring too quickly.

**Solution:**
```bash
# Check Redis connection
redis-cli -h localhost -p 6379 -a firefly123 PING
# Expected: PONG

# Check if sessions are being stored
redis-cli -h localhost -p 6379 -a firefly123 KEYS "firefly:session:*"

# Check TTL
redis-cli -h localhost -p 6379 -a firefly123 TTL "firefly:session:YOUR_SESSION_ID"
```

#### 4. Downstream services not available

**Problem:** Customer/Contract/Product services are not running.

**Impact:** Authentication will fail if customer-mgmt is not available (required for user-to-party mapping).

**Solution:**
- **customer-mgmt is required**: Must be running for authentication to succeed
- **Other services are optional**: contract-mgmt, product-mgmt, reference-master-data can be unavailable
- Check logs for errors:
  ```
  ERROR - Failed to fetch customer info for partyId: xxx
  ```
- Start required downstream services before testing authentication

#### 5. Port already in use

**Problem:** Port 8085 is already in use.

**Solution:**
```yaml
# Change port in application.yml
server:
  port: 9085
```

### Debugging Tips

#### Enable Debug Logging

```yaml
logging:
  level:
    com.firefly.security.center: DEBUG
    org.springframework.web: DEBUG
    reactor.netty: DEBUG
```

#### Check Health Endpoints

```bash
# Overall health
curl http://localhost:8085/actuator/health | jq

# Detailed health
curl http://localhost:8085/actuator/health | jq '.components'

# Metrics
curl http://localhost:8085/actuator/metrics | jq

# Specific metric (e.g., session cache hits)
curl http://localhost:8085/actuator/metrics/cache.gets | jq
```

#### Monitor Cache Performance

```bash
# Cache statistics
curl http://localhost:8085/actuator/metrics/cache.size | jq
curl http://localhost:8085/actuator/metrics/cache.gets | jq
curl http://localhost:8085/actuator/metrics/cache.puts | jq
curl http://localhost:8085/actuator/metrics/cache.evictions | jq
```

---

## Next Steps

Now that you have the Security Center running:

1. **Review API Documentation** - See [docs/API.md](API.md) for complete API reference
2. **Configure for Production** - See [docs/CONFIGURATION.md](CONFIGURATION.md) for advanced configuration
3. **Understand Architecture** - See [docs/ARCHITECTURE.md](ARCHITECTURE.md) for system design details
4. **Integrate with Your Services** - Follow the [Integration Guide](#integration-with-other-services) above
5. **Set Up Monitoring** - Configure Prometheus/Grafana for metrics

---

## Support

- **Documentation**: [docs/](.)
- **Issues**: [GitHub Issues](https://github.com/firefly-oss/domain-core-security-center/issues)
- **API Reference**: [docs/API.md](API.md)
- **Troubleshooting**: [docs/TROUBLESHOOTING.md](TROUBLESHOOTING.md)

