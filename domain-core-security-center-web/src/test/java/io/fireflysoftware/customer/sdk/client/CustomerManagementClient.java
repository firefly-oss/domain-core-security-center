/*
 * Copyright 2025 Firefly Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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
