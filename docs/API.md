# Security Center - API Documentation

## Base URLs

**Security Center:**
```
http://localhost:8085
```

**Authentication Endpoints:**
```
http://localhost:8085/api/v1/auth
```

**Session Management Endpoints:**
```
http://localhost:8085/api/v1/sessions
```

## Authentication Endpoints

### 1. Login

Authenticate a user and create a new session.

**Endpoint:** `POST /api/v1/auth/login`

**Request:**
```json
{
  "username": "user@example.com",
  "password": "password123",
  "scope": "openid profile email"
}
```

**Response:** `200 OK`
```json
{
  "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "idToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "sessionId": "550e8400-e29b-41d4-a716-446655440000",
  "partyId": "123e4567-e89b-12d3-a456-426614174000"
}
```

**Error Responses:**

- `401 Unauthorized` - Invalid credentials
```json
{
  "error": "AUTHENTICATION_FAILED",
  "message": "Invalid username or password"
}
```

- `400 Bad Request` - Missing required fields
```json
{
  "error": "INVALID_REQUEST",
  "message": "Username and password are required"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8085/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user@example.com",
    "password": "password123",
    "scope": "openid profile email"
  }'
```

---

### 2. Refresh Token

Refresh an access token using a refresh token.

**Endpoint:** `POST /api/v1/auth/refresh`

**Request:**
```json
{
  "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response:** `200 OK`
```json
{
  "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "idToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

**Error Responses:**

- `401 Unauthorized` - Invalid or expired refresh token
```json
{
  "error": "INVALID_TOKEN",
  "message": "Refresh token is invalid or expired"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8085/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
  }'
```

---

### 3. Logout

Logout a user and invalidate their session.

**Endpoint:** `POST /api/v1/auth/logout`

**Request:**
```json
{
  "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "sessionId": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Response:** `204 No Content`

(No response body on success.)

**Error Responses:**

- `500 Internal Server Error` - Logout failed
```json
{
  "error": "INTERNAL_ERROR",
  "message": "Logout operation failed"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8085/api/v1/auth/logout \
  -H "Content-Type: application/json" \
  -d '{
    "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
    "sessionId": "550e8400-e29b-41d4-a716-446655440000"
  }'
```

---

### 4. Introspect Token

Validate an IDP access token and get its details.

**Endpoint:** `POST /api/v1/auth/introspect`

**Query Parameters:**
- `accessToken` (string, required) - The access token to introspect

**Response:** `200 OK`
```json
{
  "active": true,
  "username": "testuser",
  "exp": 1698353640
}
```

**cURL Example:**
```bash
curl -X POST "http://localhost:8085/api/v1/auth/introspect?accessToken=eyJhbGci..."
```

---

### 5. Reset Password

Trigger IDP password reset flow for a user.

**Endpoint:** `POST /api/v1/auth/reset-password`

**Query Parameters:**
- `userName` (string, required) - The username to reset password for

**Response:** `204 No Content`

**cURL Example:**
```bash
curl -X POST "http://localhost:8085/api/v1/auth/reset-password?userName=testuser"
```

---

## User Management Endpoints

### Create User

Create a new user in the IDP.

**Endpoint:** `POST /api/v1/users`

**Request:**
```json
{
  "username": "newuser",
  "email": "newuser@example.com",
  "password": "SecurePass123!"
}
```

**Response:** `200 OK`
Returns the created user details (structure depends on IDP adapter implementation).

**cURL Example:**
```bash
curl -X POST http://localhost:8085/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "email": "newuser@example.com",
    "password": "SecurePass123!"
  }'
```

---

## Session Management Endpoints

### 1. Get Session by ID

Retrieve session details by session ID.

**Endpoint:** `GET /api/v1/sessions/{sessionId}`

**Response:** `200 OK`
```json
{
  "sessionId": "550e8400-e29b-41d4-a716-446655440000",
  "partyId": "123e4567-e89b-12d3-a456-426614174000",
  "customerInfo": {
    "partyId": "123e4567-e89b-12d3-a456-426614174000",
    "partyKind": "INDIVIDUAL",
    "tenantId": "tenant-123",
    "fullName": "John Doe",
    "preferredLanguage": "en",
    "email": "john.doe@example.com",
    "phoneNumber": "+1234567890",
    "isActive": true
  },
  "activeContracts": [
    {
      "contractId": "789e0123-e45b-67d8-a901-234567890abc",
      "contractNumber": "CNT-2025-001",
      "contractStatus": "ACTIVE",
      "startDate": "2025-01-01T00:00:00",
      "contractPartyId": "cp-123",
      "product": {
        "productId": "456e7890-e12b-34d5-a678-901234567def",
        "productName": "Premium Checking Account",
        "productCode": "CHK-PREM",
        "productType": "CHECKING",
        "productStatus": "ACTIVE"
      },
      "roleInContract": {
        "roleId": "role-123",
        "roleCode": "PRIMARY_HOLDER",
        "name": "Primary Account Holder",
        "isActive": true,
        "scopes": [
          {
            "scopeId": "scope-1",
            "roleId": "role-123",
            "scopeCode": "READ_BALANCE",
            "scopeName": "Read Balance",
            "actionType": "READ",
            "resourceType": "BALANCE",
            "isActive": true
          },
          {
            "scopeId": "scope-2",
            "roleId": "role-123",
            "scopeCode": "WRITE_TRANSACTION",
            "scopeName": "Write Transaction",
            "actionType": "WRITE",
            "resourceType": "TRANSACTION",
            "isActive": true
          }
        ]
      },
      "isActive": true,
      "createdAt": "2025-01-01T00:00:00"
    }
  ],
  "createdAt": "2025-10-26T19:54:00",
  "lastAccessedAt": "2025-10-26T20:54:00",
  "expiresAt": "2025-10-26T21:24:00",
  "ipAddress": "192.168.1.100",
  "userAgent": "Mozilla/5.0...",
  "status": "ACTIVE"
}
```

**Error Responses:**

- `404 Not Found` - Session not found
```json
{
  "error": "SESSION_NOT_FOUND",
  "message": "Session not found or expired"
}
```

- `401 Unauthorized` - Missing or invalid authorization header
```json
{
  "error": "UNAUTHORIZED",
  "message": "Authorization header is required"
}
```

**cURL Example:**
```bash
curl -X GET http://localhost:8085/api/v1/sessions/550e8400-e29b-41d4-a716-446655440000 \
  -H "Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

### 2. Create or Get Session

Create a new session or retrieve existing session from X-Party-Id header.

**Endpoint:** `POST /api/v1/sessions`

**Headers:**
```
X-Party-Id: 123e4567-e89b-12d3-a456-426614174000
```

**Response:** `200 OK`
```json
{
  "sessionId": "550e8400-e29b-41d4-a716-446655440000",
  "partyId": "123e4567-e89b-12d3-a456-426614174000",
  "customerInfo": {
    "partyId": "123e4567-e89b-12d3-a456-426614174000",
    "fullName": "John Doe",
    "email": "john.doe@example.com",
    "phoneNumber": "+1234567890"
  },
  "activeContracts": [...],
  "createdAt": "2025-10-26T19:54:00",
  "lastAccessedAt": "2025-10-26T20:54:00",
  "expiresAt": "2025-10-26T21:24:00",
  "status": "ACTIVE"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8085/api/v1/sessions \
  -H "X-Party-Id: 123e4567-e89b-12d3-a456-426614174000"
```

---

### 3. Get Session by Party ID

Retrieve session details by party ID.

**Endpoint:** `GET /api/v1/sessions/party/{partyId}`

**Response:** `200 OK`
```json
{
  "sessionId": "550e8400-e29b-41d4-a716-446655440000",
  "partyId": "123e4567-e89b-12d3-a456-426614174000",
  "customerInfo": {...},
  "activeContracts": [...],
  "status": "ACTIVE"
}
```

**Error Responses:**

- `404 Not Found` - No session found for this party

**cURL Example:**
```bash
curl -X GET http://localhost:8085/api/v1/sessions/party/123e4567-e89b-12d3-a456-426614174000
```

---

### 4. Invalidate Session

Invalidate a specific session by session ID.

**Endpoint:** `DELETE /api/v1/sessions/{sessionId}`

**Response:** `204 No Content`

**cURL Example:**
```bash
curl -X DELETE http://localhost:8085/api/v1/sessions/550e8400-e29b-41d4-a716-446655440000
```

---

### 5. Invalidate Sessions by Party ID

Invalidate all sessions for a specific party.

**Endpoint:** `DELETE /api/v1/sessions/party/{partyId}`

**Response:** `204 No Content`

**cURL Example:**
```bash
curl -X DELETE http://localhost:8085/api/v1/sessions/party/123e4567-e89b-12d3-a456-426614174000
```

---

### 6. Refresh Session

Refresh session data by re-fetching from downstream services.

**Endpoint:** `POST /api/v1/sessions/{sessionId}/refresh`

**Response:** `200 OK`
```json
{
  "sessionId": "550e8400-e29b-41d4-a716-446655440000",
  "partyId": "123e4567-e89b-12d3-a456-426614174000",
  "customerInfo": {...},
  "activeContracts": [...],
  "lastAccessedAt": "2025-10-26T20:55:00",
  "status": "ACTIVE"
}
```

**Error Responses:**

- `404 Not Found` - Session not found

**cURL Example:**
```bash
curl -X POST http://localhost:8085/api/v1/sessions/550e8400-e29b-41d4-a716-446655440000/refresh
```

---

### 7. Validate Session

Check if a session is valid and active.

**Endpoint:** `GET /api/v1/sessions/{sessionId}/validate`

**Response:** `200 OK`
```json
true
```

**cURL Example:**
```bash
curl -X GET http://localhost:8085/api/v1/sessions/550e8400-e29b-41d4-a716-446655440000/validate
```

---

### 8. Check Product Access

Check if a party has access to a specific product.

**Endpoint:** `GET /api/v1/sessions/access-check`

**Query Parameters:**
- `partyId` (UUID, required) - Party ID
- `productId` (UUID, required) - Product ID

**Response:** `200 OK`
```json
true
```

**cURL Example:**
```bash
curl -X GET "http://localhost:8085/api/v1/sessions/access-check?partyId=123e4567-e89b-12d3-a456-426614174000&productId=456e7890-e12b-34d5-a678-901234567def"
```

---

### 9. Check Permission

Check if a party has a specific permission on a product.

**Endpoint:** `GET /api/v1/sessions/permission-check`

**Query Parameters:**
- `partyId` (UUID, required) - Party ID
- `productId` (UUID, required) - Product ID
- `actionType` (string, required) - Action type (e.g., READ, WRITE, DELETE)
- `resourceType` (string, optional) - Resource type (e.g., BALANCE, TRANSACTION)

**Response:** `200 OK`
```json
true
```

**cURL Example:**
```bash
curl -X GET "http://localhost:8085/api/v1/sessions/permission-check?partyId=123e4567-e89b-12d3-a456-426614174000&productId=456e7890-e12b-34d5-a678-901234567def&actionType=READ&resourceType=BALANCE"
```

---

## Health Check Endpoint

### Health Check

Check the health status of the Security Center service.

**Endpoint:** `GET /actuator/health`

**Response:** `200 OK`
```json
{
  "status": "UP",
  "components": {
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 500000000000,
        "free": 250000000000,
        "threshold": 10485760
      }
    },
    "redis": {
      "status": "UP",
      "details": {
        "version": "7.0.0"
      }
    },
    "idpProvider": {
      "status": "UP",
      "details": {
        "provider": "keycloak",
        "url": "http://localhost:8080"
      }
    }
  }
}
```

**Degraded Status:** `200 OK` (Service still operational)
```json
{
  "status": "UP",
  "components": {
    "redis": {
      "status": "DOWN",
      "details": {
        "error": "Connection refused"
      }
    }
  }
}
```

**cURL Example:**
```bash
curl -X GET http://localhost:8085/actuator/health
```

---

## Data Models

### LoginRequest

Used by `POST /api/v1/auth/login`. The actual Java class is `org.fireflyframework.idp.dtos.LoginRequest`.

```typescript
interface LoginRequest {
  username: string;       // Required: User's email or username
  password: string;       // Required: User's password
  scope?: string;         // Optional: OAuth 2.0 scopes
}
```

### AuthenticationResponse

Returned by login and refresh endpoints. The actual Java class is `AuthenticationService.AuthenticationResponse` (inner class).

```typescript
interface AuthenticationResponse {
  accessToken: string;    // JWT access token
  refreshToken: string;   // JWT refresh token
  idToken: string;        // JWT ID token
  tokenType: string;      // Token type (e.g., "Bearer")
  expiresIn: number;      // Token expiration in seconds (int64)
  sessionId: string;      // Session ID (format: "session_{partyId}_{timestamp}")
  partyId: string;        // UUID of authenticated party
}
```

### RefreshRequest

Used by `POST /api/v1/auth/refresh`. The actual Java class is `org.fireflyframework.idp.dtos.RefreshRequest`.

```typescript
interface RefreshRequest {
  refreshToken: string;   // Required: Valid refresh token
}
```

### AuthLogoutRequest

Used by `POST /api/v1/auth/logout`. The actual Java class is `AuthenticationService.AuthLogoutRequest` (inner class).

```typescript
interface AuthLogoutRequest {
  accessToken: string;    // Required: Current access token
  refreshToken: string;   // Required: Current refresh token
  sessionId: string;      // Required: Session ID to invalidate
}
```

### SessionContext

```typescript
interface SessionContextDTO {
  sessionId: string;
  partyId: string;                   // UUID
  customerInfo: CustomerInfoDTO;
  activeContracts: ContractInfoDTO[];
  createdAt: string;                 // ISO 8601 timestamp (LocalDateTime)
  lastAccessedAt: string;            // ISO 8601 timestamp (LocalDateTime)
  expiresAt: string;                 // ISO 8601 timestamp (LocalDateTime)
  ipAddress: string;
  userAgent: string;
  status: "ACTIVE" | "EXPIRED" | "INVALIDATED" | "LOCKED";
  metadata: SessionMetadataDTO;
}

interface CustomerInfoDTO {
  partyId: string;
  partyKind: string;                 // INDIVIDUAL or ORGANIZATION
  tenantId: string;                  // UUID
  fullName: string;
  preferredLanguage: string;
  email: string;
  phoneNumber: string;
  taxIdNumber: string;
  isActive: boolean;
}

interface ContractInfoDTO {
  contractId: string;                // UUID
  contractNumber: string;
  contractStatus: string;
  startDate: string;                 // ISO 8601 timestamp (LocalDateTime)
  endDate: string;                   // ISO 8601 timestamp (LocalDateTime)
  contractPartyId: string;           // UUID
  roleInContract: RoleInfoDTO;
  product: ProductInfoDTO;
  dateJoined: string;                // ISO 8601 timestamp
  dateLeft: string;                  // ISO 8601 timestamp
  isActive: boolean;
  createdAt: string;                 // ISO 8601 timestamp
  updatedAt: string;                 // ISO 8601 timestamp
}

interface ProductInfoDTO {
  productId: string;                 // UUID
  productCatalogId: string;          // UUID
  productSubtypeId: string;          // UUID
  productName: string;
  productCode: string;
  productDescription: string;
  productType: string;
  productStatus: string;
  launchDate: string;                // ISO 8601 date (LocalDate)
  endDate: string;                   // ISO 8601 date (LocalDate)
  dateCreated: string;               // ISO 8601 timestamp (LocalDateTime)
  dateUpdated: string;               // ISO 8601 timestamp (LocalDateTime)
}

interface RoleInfoDTO {
  roleId: string;                    // UUID
  roleCode: string;
  name: string;
  description: string;
  isActive: boolean;
  scopes: RoleScopeInfoDTO[];
  dateCreated: string;               // ISO 8601 timestamp (LocalDateTime)
  dateUpdated: string;               // ISO 8601 timestamp (LocalDateTime)
}

interface RoleScopeInfoDTO {
  scopeId: string;                   // UUID
  roleId: string;                    // UUID
  scopeCode: string;
  scopeName: string;
  description: string;
  actionType: string;                // READ, WRITE, DELETE, EXECUTE, APPROVE
  resourceType: string;              // PRODUCT, TRANSACTION, ACCOUNT, BALANCE
  isActive: boolean;
}

interface SessionMetadataDTO {
  channel: string;                   // web, mobile, api
  sourceApplication: string;
  deviceInfo: string;
  geolocation: string;
}
```

---

## HTTP Status Codes

| Code | Description |
|------|-------------|
| 200  | Success |
| 400  | Bad Request - Invalid input |
| 401  | Unauthorized - Authentication failed |
| 404  | Not Found - Resource not found |
| 500  | Internal Server Error |
| 503  | Service Unavailable - Downstream service error |

---

## Error Response Format

All error responses follow this format:

```json
{
  "error": "ERROR_CODE",
  "message": "Human-readable error message",
  "timestamp": "2025-10-26T19:54:00Z",
  "path": "/api/v1/auth/login"
}
```

### Common Error Codes

| Code | Description |
|------|-------------|
| `AUTHENTICATION_FAILED` | Invalid credentials |
| `INVALID_TOKEN` | Token is invalid or expired |
| `INVALID_REQUEST` | Missing required fields |
| `SESSION_NOT_FOUND` | Session does not exist or expired |
| `UNAUTHORIZED` | Missing authorization |
| `IDP_ERROR` | Identity provider error |
| `CACHE_ERROR` | Cache operation failed |
| `INTERNAL_ERROR` | Unexpected server error |

---

## Rate Limiting

Currently not implemented. Recommended for production:

- **Login**: 5 attempts per minute per IP
- **Refresh**: 10 requests per minute per token
- **Session retrieval**: 60 requests per minute per token

---

## CORS Configuration

In production, configure allowed origins:

```yaml
spring:
  webflux:
    cors:
      allowed-origins: "https://app.example.com"
      allowed-methods: "GET,POST"
      allowed-headers: "*"
      allow-credentials: true
```

---

## Authentication Flow Example

```mermaid
sequenceDiagram
    Client->>Security Center: POST /api/v1/auth/login
    Security Center->>IDP: Authenticate
    IDP-->>Security Center: Tokens
    Security Center->>Customer Mgmt: Get customer
    Security Center->>Contract Mgmt: Get contracts
    Security Center->>Cache: Cache session
    Security Center-->>Client: AuthenticationResponse

    Client->>Security Center: GET /api/v1/sessions/{id}
    Security Center->>Cache: Retrieve session
    Cache-->>Security Center: SessionContextDTO
    Security Center-->>Client: SessionContextDTO

    Client->>Security Center: POST /api/v1/auth/logout
    Security Center->>IDP: Revoke tokens
    Security Center->>Cache: Delete session
    Security Center-->>Client: 204 No Content
```

---

## SDK Usage (For Other Microservices)

```java
@Autowired
private FireflySessionManager sessionManager;

// Create or retrieve session from request
public Mono<SessionContextDTO> handleRequest(ServerWebExchange exchange) {
    return sessionManager.createOrGetSession(exchange)
        .flatMap(session -> {
            // Use session for authorization
            boolean hasAccess = checkAccess(session);
            if (!hasAccess) {
                return Mono.error(new UnauthorizedException());
            }
            return processRequest(session);
        });
}
```

---

## Best Practices

1. **Always use HTTPS** in production
2. **Store tokens securely** on the client (HttpOnly cookies recommended)
3. **Implement token refresh** before expiration
4. **Handle 401 errors** by refreshing or re-authenticating
5. **Use correlation IDs** for request tracing
6. **Implement exponential backoff** for retries
7. **Validate tokens** on every request
8. **Monitor failed authentication attempts**
