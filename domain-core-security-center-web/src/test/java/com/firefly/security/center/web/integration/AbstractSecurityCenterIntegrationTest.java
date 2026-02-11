/*
 * Copyright 2025 Firefly Software Solutions Inc
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

package com.firefly.security.center.web.integration;

import com.firefly.common.product.sdk.api.ProductApi;
import com.firefly.common.reference.master.data.sdk.api.ContractRoleApi;
import com.firefly.common.reference.master.data.sdk.api.ContractRoleScopeApi;
import com.firefly.core.contract.sdk.api.ContractsApi;
import com.firefly.core.contract.sdk.api.GlobalContractPartiesApi;
import com.firefly.core.customer.sdk.api.*;
import com.firefly.security.center.web.config.SecurityCenterTestConfiguration;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

/**
 * Abstract base class for Security Center integration tests.
 * 
 * <p>Provides:
 * <ul>
 *   <li>Spring Boot Test context</li>
 *   <li>Testcontainers support</li>
 *   <li>Mocked SDK clients (Customer, Contract, Product, Reference Data)</li>
 *   <li>Dynamic property configuration</li>
 * </ul>
 * 
 * <p><strong>Note:</strong> These tests are disabled by default (@Disabled on subclasses).
 * They require Docker to be running and may require additional configuration (e.g., LocalStack PRO token).
 * 
 * <p>Subclasses should:
 * <ul>
 *   <li>Set up specific containers (Keycloak, LocalStack, Redis)</li>
 *   <li>Configure IDP-specific properties</li>
 *   <li>Implement test scenarios</li>
 * </ul>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Tag("integration")
@Import(SecurityCenterTestConfiguration.class)
public abstract class AbstractSecurityCenterIntegrationTest {

    // Mock Customer Management SDK APIs
    @MockBean
    protected PartiesApi partiesApi;
    
    @MockBean
    protected NaturalPersonsApi naturalPersonsApi;
    
    @MockBean
    protected LegalEntitiesApi legalEntitiesApi;
    
    @MockBean
    protected EmailContactsApi emailContactsApi;
    
    @MockBean
    protected PhoneContactsApi phoneContactsApi;

    // Mock Contract Management SDK APIs
    @MockBean
    protected ContractsApi contractsApi;
    
    @MockBean
    protected GlobalContractPartiesApi globalContractPartiesApi;

    // Mock Product Management SDK API
    @MockBean
    protected ProductApi productApi;

    // Mock Reference Master Data SDK APIs
    @MockBean
    protected ContractRoleApi contractRoleApi;

    @MockBean
    protected ContractRoleScopeApi contractRoleScopeApi;

    protected UUID testPartyId;



    /**
     * Configure dynamic properties for integration tests.
     * Subclasses can override to add IDP-specific or cache-specific properties.
     *
     * @param registry Dynamic property registry
     */
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Common properties for all integration tests
        registry.add("spring.main.allow-bean-definition-overriding", () -> "true");
        registry.add("logging.level.com.firefly.security.center", () -> "DEBUG");
        registry.add("logging.level.org.testcontainers", () -> "INFO");
        registry.add("logging.level.com.github.dockerjava", () -> "WARN");

        // Session configuration
        registry.add("firefly.security-center.session.timeout-minutes", () -> "30");
        registry.add("firefly.security-center.session.cleanup-interval-minutes", () -> "15");

        // SDK Client base URLs (mocked - not actually called in tests)
        registry.add("firefly.clients.contract-mgmt.base-url", () -> "http://localhost:8081");
        registry.add("firefly.clients.customer-mgmt.base-url", () -> "http://localhost:8082");
        registry.add("firefly.clients.product-mgmt.base-url", () -> "http://localhost:8083");
        registry.add("firefly.clients.reference-master-data.base-url", () -> "http://localhost:8084");
    }
}
