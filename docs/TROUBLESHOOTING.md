# Security Center - Troubleshooting Guide

## Common Issues and Solutions

### Build and Dependencies

#### Issue: Bean Definition Override Exception

**Symptom:**
```
BeanDefinitionOverrideException: Invalid bean definition with name 'redisConnectionFactory'
```

**Cause:** Spring Boot's `RedisAutoConfiguration` conflicts with [`fireflyframework-cache`](https://github.com/fireflyframework/fireflyframework-cache).

**Solution:**
Exclude Spring Boot's Redis auto-configuration:

```java
@SpringBootTest
@EnableAutoConfiguration(exclude = {
    RedisAutoConfiguration.class,
    RedisReactiveAutoConfiguration.class
})
class YourTest {
    // Test code
}
```

---

#### Issue: KeycloakProperties Bean Not Found

**Symptom:**
```
UnsatisfiedDependencyException: Could not autowire KeycloakAPIFactory
No qualifying bean of type 'KeycloakProperties' available
```

**Cause:** `@ConfigurationProperties` not enabled for `KeycloakProperties`.

**Solution:**
Ensure `KeycloakAutoConfiguration` exists:

```java
@Configuration
@EnableConfigurationProperties(KeycloakProperties.class)
public class KeycloakAutoConfiguration {
    // Configuration
}
```

---

### Identity Provider Integration

#### Issue: Keycloak 401 Unauthorized on getUserInfo/Logout

**Symptom:**
```
HTTP 401 Unauthorized when calling /userinfo or /logout
```

**Cause:** Missing "Bearer " prefix in Authorization header.

**Solution:**
Update Authorization header format:

```java
String authHeader = accessToken;
if (authHeader != null && !authHeader.toLowerCase().startsWith("bearer ")) {
    authHeader = "Bearer " + authHeader;
}
.header("Authorization", authHeader)
```

---

#### Issue: AWS Cognito LocalStack Connection Refused

**Symptom:**
```
Connection refused: localhost:4566
```

**Cause:** LocalStack not running or endpoint not properly configured.

**Solution:**

1. Ensure LocalStack is running:
```bash
docker ps | grep localstack
```

2. Set endpoint override in test configuration:
```yaml
firefly:
  security-center:
    idp:
      cognito:
        endpoint-override: http://localhost:4566
```

3. Ensure `LOCALSTACK_AUTH_TOKEN` is set:
```bash
export LOCALSTACK_AUTH_TOKEN="your-token"
```

---

#### Issue: Invalid Client Secret in Keycloak

**Symptom:**
```
invalid_client: Invalid client credentials
```

**Cause:** Client secret in configuration doesn't match Keycloak client.

**Solution:**

1. Check Keycloak client secret in Admin Console
2. Update `application.yml`:
```yaml
keycloak:
  client-secret: correct-secret-from-keycloak
```

For tests, ensure realm import JSON has matching secret:
```json
{
  "clientId": "security-center-test",
  "secret": "test-secret-123"
}
```

---

### Redis and Caching

#### Issue: Redis Container Fails to Start (Testcontainers)

**Symptom:**
```
Container startup failed: Timed out waiting for log output matching 'Ready to accept connections'
```

**Cause:** Custom Redis command suppresses startup log.

**Solution:**
Remove custom `--loglevel` command:

```java
// Wrong
static RedisContainer redis = new RedisContainer(DockerImageName.parse("redis:7-alpine"))
    .withCommand("redis-server", "--loglevel", "warning");

// Correct
static RedisContainer redis = new RedisContainer(DockerImageName.parse("redis:7-alpine"));
```

---

#### Issue: Redis Connection Timeout

**Symptom:**
```
RedisConnectionException: Unable to connect to Redis
```

**Cause:** Redis not running or wrong host/port.

**Solution:**

1. Check Redis is running:
```bash
redis-cli ping
# Should return: PONG
```

2. Verify connection details:
```yaml
firefly:
  cache:
    redis:
      host: localhost  # Correct host
      port: 6379       # Correct port
```

3. Check firewall rules allow connection to Redis port

---

#### Issue: Cache Not Working (Using Caffeine Instead of Redis)

**Symptom:**
Sessions not persisting across application restarts.

**Cause:** Caffeine (in-memory) cache configured instead of Redis.

**Solution:**
Set Redis as default cache type:

```yaml
firefly:
  cache:
    default-cache-type: REDIS  # Not CAFFEINE
    redis:
      enabled: true
```

---

### Testing

#### Issue: Keycloak Integration Tests Failing

**Symptom:**
```
401 Unauthorized during authentication in tests
```

**Cause:** Test realm not properly imported or user credentials mismatch.

**Solution:**

1. Ensure `keycloak-realm-test.json` is in `src/test/resources/`
2. Verify test user credentials:
```java
// In realm JSON
{
  "username": "testuser",
  "credentials": [{"value": "Test123!@#"}]
}

// In test
authRequest.setUsername("testuser");
authRequest.setPassword("Test123!@#");
```

3. Check realm import in test configuration:
```java
@Container
static KeycloakContainer keycloak = new KeycloakContainer()
    .withRealmImportFile("/keycloak-realm-test.json");
```

---

#### Issue: LocalStack Cognito Tests Failing

**Symptom:**
```
User pool not found or creation failed
```

**Cause:** Missing LocalStack PRO token or incorrect configuration.

**Solution:**

1. Set LocalStack PRO token:
```bash
export LOCALSTACK_AUTH_TOKEN="your-token"
```

2. Ensure LocalStack container uses PRO image:
```java
@Container
static LocalStackContainer localstack = new LocalStackContainer(
    DockerImageName.parse("localstack/localstack-pro:latest")
)
.withEnv("LOCALSTACK_AUTH_TOKEN", System.getenv("LOCALSTACK_AUTH_TOKEN"))
.withServices(Service.COGNITO_IDP);
```

3. Check LocalStack logs:
```bash
docker logs <localstack-container-id>
```

---

### Configuration

#### Issue: Wrong Property Prefix

**Symptom:**
```
Configuration properties not loading
```

**Cause:** Using wrong property prefix in YAML.

**Solution:**
Use correct prefixes. The IDP provider is selected with `firefly.security-center.idp.provider`, and
the Keycloak-specific properties are loaded by the framework library's own configuration (typically
under `keycloak.*` or as defined by the `lib-idp-keycloak-impl` library). Cognito and cache properties:

- **IDP provider selection:** `firefly.security-center.idp.provider`
- **Cognito:** Properties defined by `lib-idp-aws-cognito-impl`
- **Redis/Cache:** `firefly.cache.*` (via `fireflyframework-cache`)

Check the test configuration at `application-test.yml` for a working example:
```yaml
firefly:
  security-center:
    idp:
      provider: keycloak
```

---

#### Issue: IDP Provider Not Switching

**Symptom:**
Application still using old IDP after configuration change.

**Cause:** Provider property not updated or application not restarted.

**Solution:**

1. Update provider property:
```yaml
firefly:
  security-center:
    idp:
      provider: cognito  # or "keycloak"
```

2. Restart application
3. Verify in logs:
```
INFO: Selected IDP provider: cognito
```

---

### Runtime Errors

#### Issue: Session Not Found After Login

**Symptom:**
```
404 Not Found when retrieving session immediately after login
```

**Cause:** Cache write latency or session not properly cached.

**Solution:**

1. Check cache configuration is correct
2. Verify cache health:
```bash
curl http://localhost:8085/actuator/health
```

3. Check logs for cache errors:
```bash
grep "CacheError" application.log
```

---

#### Issue: Token Expired Error

**Symptom:**
```
401 Unauthorized: Token has expired
```

**Cause:** Token TTL exceeded.

**Solution:**

1. Implement token refresh before expiration:
```java
// Check expiration and refresh if needed
if (isTokenExpiringSoon(session.getExpiresAt())) {
    refreshToken(session.getRefreshToken());
}
```

2. Configure appropriate token TTL in IDP
3. Implement sliding session expiration

---

#### Issue: Downstream Service Unavailable

**Symptom:**
```
503 Service Unavailable
Error: Unable to fetch customer data
```

**Cause:** Downstream service (customer-mgmt, contract-mgmt, etc.) is down.

**Solution:**

1. Check service health:
```bash
curl http://customer-mgmt-url/actuator/health
```

2. Verify service URLs in configuration:
```yaml
firefly:
  security-center:
    clients:
      customer-mgmt:
        base-url: http://correct-url:8081
```

3. Check network connectivity
4. Implement circuit breaker for resilience (future enhancement)

---

### Performance Issues

#### Issue: Slow Login Response

**Symptom:**
Login taking 5+ seconds to complete.

**Cause:** Sequential enrichment calls to downstream services.

**Solution:**

Parallel enrichment is already implemented, but verify:

```java
// Ensure using Mono.zip for parallel execution
return Mono.zip(
    customerService.getCustomer(partyId),
    contractService.getContracts(partyId)
).map(tuple -> buildSession(tuple.getT1(), tuple.getT2()));
```

Check downstream service response times.

---

#### Issue: High Redis Memory Usage

**Symptom:**
Redis memory usage growing unbounded.

**Cause:** Session TTL not properly set or too many sessions.

**Solution:**

1. Configure session TTL:
```yaml
firefly:
  security-center:
    session:
      default-ttl: 3600  # 1 hour
```

2. Monitor Redis memory:
```bash
redis-cli INFO memory
```

3. Set Redis maxmemory and eviction policy:
```bash
redis-cli CONFIG SET maxmemory 2gb
redis-cli CONFIG SET maxmemory-policy allkeys-lru
```

---

## Debugging Tips

### Enable Debug Logging

```yaml
logging:
  level:
    com.firefly.security.center: DEBUG
    com.firefly.security.center.core.services: TRACE
    org.springframework.cache: DEBUG
    io.lettuce.core: DEBUG
```

### Check Actuator Endpoints

```bash
# Health check
curl http://localhost:8085/actuator/health

# Metrics
curl http://localhost:8085/actuator/metrics

# Info
curl http://localhost:8085/actuator/info
```

### Verify IDP Configuration

```bash
# Keycloak
curl http://keycloak-url/realms/your-realm/.well-known/openid-configuration

# Cognito (with AWS CLI)
aws cognito-idp describe-user-pool --user-pool-id us-east-1_XXXXXX
```

### Test Redis Connection

```bash
redis-cli -h localhost -p 6379 ping
redis-cli -h localhost -p 6379 KEYS "firefly:session:*"
```

### Inspect Docker Containers (Tests)

```bash
# List running containers
docker ps

# View container logs
docker logs <container-id>

# Inspect container
docker inspect <container-id>
```

---

## Getting Help

### Logs to Collect

When reporting issues, include:

1. **Application logs** with DEBUG level
2. **Stack traces** for exceptions
3. **Configuration** (sanitize secrets!)
4. **Environment details** (Java version, OS, Docker version)
5. **Test output** if applicable

### Useful Commands

```bash
# Check Java version
java -version

# Check Maven version
mvn -version

# Run tests with debug output
mvn test -X -Dtest=ClassName

# Check Docker
docker --version
docker ps

# Check Redis
redis-cli --version
redis-cli ping
```

---

## Known Limitations

1. **Rate Limiting:** Not implemented - add in production
2. **Circuit Breaker:** Not implemented for downstream services
3. **Distributed Tracing:** Basic correlation IDs only
4. **Token Revocation:** IDP-dependent, may have latency
5. **Session Replication:** Requires Redis Cluster for HA

---

## Best Practices to Avoid Issues

1. Always use environment variables for secrets
2. Test configuration changes in dev environment first
3. Monitor cache hit rates and adjust TTL accordingly
4. Implement proper error handling for downstream services
5. Use health checks in load balancers
6. Set up alerting for authentication failures
7. Regularly rotate IDP client secrets
8. Keep dependencies up to date
9. Run integration tests before deployment
10. Enable TLS for Redis and IDP in production
