package io.fireflysoftware.customer.sdk.client;

import io.fireflysoftware.customer.sdk.dto.CustomerResponse;
import reactor.core.publisher.Mono;

/**
 * Test wrapper interface for Customer Management SDK.
 * 
 * <p>This interface is used for mocking in integration tests.
 * In production, the Security Center uses the real OpenAPI-generated SDK from
 * core-common-customer-mgmt-sdk module (io.fireflysoftware.core.customer.sdk.api.PartiesApi).
 * 
 * <p>This wrapper simplifies test mocking while the real implementation would delegate to the actual SDK.
 */
public interface CustomerManagementClient {
    Mono<CustomerResponse> getCustomerByUsername(String username);
}
