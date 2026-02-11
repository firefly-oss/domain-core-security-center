package com.firefly.customer.sdk.client;

import com.firefly.customer.sdk.dto.CustomerResponse;
import reactor.core.publisher.Mono;

/**
 * Test wrapper interface for Customer Management SDK.
 * 
 * <p>This interface is used for mocking in integration tests.
 * In production, the Security Center uses the real OpenAPI-generated SDK from
 * common-platform-customer-mgmt-sdk module (com.firefly.core.customer.sdk.api.PartiesApi).
 * 
 * <p>This wrapper simplifies test mocking while the real implementation would delegate to the actual SDK.
 */
public interface CustomerManagementClient {
    Mono<CustomerResponse> getCustomerByUsername(String username);
}
