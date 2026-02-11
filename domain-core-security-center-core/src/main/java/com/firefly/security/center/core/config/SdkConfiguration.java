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

package com.firefly.security.center.core.config;

import com.firefly.common.product.sdk.api.ProductApi;
import com.firefly.common.reference.master.data.sdk.api.ContractRoleApi;
import com.firefly.common.reference.master.data.sdk.api.ContractRoleScopeApi;
import com.firefly.core.contract.sdk.api.ContractPartiesApi;
import com.firefly.core.contract.sdk.api.ContractsApi;
import com.firefly.core.contract.sdk.api.GlobalContractPartiesApi;
import com.firefly.core.customer.sdk.api.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for OpenAPI-generated SDK clients for downstream microservices.
 * 
 * <p>This configuration creates ApiClient beans for each management service and
 * instantiates the corresponding API classes. The ApiClients are configured with
 * base URLs from application properties.
 * 
 * <p><strong>Required Configuration:</strong></p>
 * <pre>
 * firefly:
 *   security-center:
 *     clients:
 *       customer-mgmt:
 *         base-url: http://localhost:8081
 *       contract-mgmt:
 *         base-url: http://localhost:8082
 *       product-mgmt:
 *         base-url: http://localhost:8083
 *       reference-master-data:
 *         base-url: http://localhost:8084
 * </pre>
 */
@Configuration
@Slf4j
public class SdkConfiguration {

    // ========== Customer Management SDK ==========
    
    @Bean(name = "customerMgmtApiClient")
    public com.firefly.core.customer.sdk.invoker.ApiClient customerMgmtApiClient(
            @Value("${firefly.security-center.clients.customer-mgmt.base-url}") String baseUrl) {
        log.info("Configuring Customer Management SDK with base URL: {}", baseUrl);
        com.firefly.core.customer.sdk.invoker.ApiClient client =
            new com.firefly.core.customer.sdk.invoker.ApiClient();
        client.setBasePath(baseUrl);
        return client;
    }
    
    @Bean
    public PartiesApi partiesApi(
            @Qualifier("customerMgmtApiClient") com.firefly.core.customer.sdk.invoker.ApiClient client) {
        return new PartiesApi(client);
    }
    
    @Bean
    public NaturalPersonsApi naturalPersonsApi(
            @Qualifier("customerMgmtApiClient") com.firefly.core.customer.sdk.invoker.ApiClient client) {
        return new NaturalPersonsApi(client);
    }
    
    @Bean
    public LegalEntitiesApi legalEntitiesApi(
            @Qualifier("customerMgmtApiClient") com.firefly.core.customer.sdk.invoker.ApiClient client) {
        return new LegalEntitiesApi(client);
    }
    
    @Bean
    public EmailContactsApi emailContactsApi(
            @Qualifier("customerMgmtApiClient") com.firefly.core.customer.sdk.invoker.ApiClient client) {
        return new EmailContactsApi(client);
    }
    
    @Bean
    public PhoneContactsApi phoneContactsApi(
            @Qualifier("customerMgmtApiClient") com.firefly.core.customer.sdk.invoker.ApiClient client) {
        return new PhoneContactsApi(client);
    }

    // ========== Contract Management SDK ==========
    
    @Bean(name = "contractMgmtApiClient")
    public com.firefly.core.contract.sdk.invoker.ApiClient contractMgmtApiClient(
            @Value("${firefly.security-center.clients.contract-mgmt.base-url}") String baseUrl) {
        log.info("Configuring Contract Management SDK with base URL: {}", baseUrl);
        com.firefly.core.contract.sdk.invoker.ApiClient client =
            new com.firefly.core.contract.sdk.invoker.ApiClient();
        client.setBasePath(baseUrl);
        return client;
    }
    
    @Bean
    public ContractsApi contractsApi(
            @Qualifier("contractMgmtApiClient") com.firefly.core.contract.sdk.invoker.ApiClient client) {
        return new ContractsApi(client);
    }
    
    @Bean
    public ContractPartiesApi contractPartiesApi(
            @Qualifier("contractMgmtApiClient") com.firefly.core.contract.sdk.invoker.ApiClient client) {
        return new ContractPartiesApi(client);
    }
    
    @Bean
    public GlobalContractPartiesApi globalContractPartiesApi(
            @Qualifier("contractMgmtApiClient") com.firefly.core.contract.sdk.invoker.ApiClient client) {
        return new GlobalContractPartiesApi(client);
    }

    // ========== Product Management SDK ==========
    
    @Bean(name = "productMgmtApiClient")
    public com.firefly.common.product.sdk.invoker.ApiClient productMgmtApiClient(
            @Value("${firefly.security-center.clients.product-mgmt.base-url}") String baseUrl) {
        log.info("Configuring Product Management SDK with base URL: {}", baseUrl);
        com.firefly.common.product.sdk.invoker.ApiClient client =
            new com.firefly.common.product.sdk.invoker.ApiClient();
        client.setBasePath(baseUrl);
        return client;
    }
    
    @Bean
    public ProductApi productApi(
            @Qualifier("productMgmtApiClient") com.firefly.common.product.sdk.invoker.ApiClient client) {
        return new ProductApi(client);
    }

    // ========== Reference Master Data SDK ==========
    
    @Bean(name = "referenceMasterDataApiClient")
    public com.firefly.common.reference.master.data.sdk.invoker.ApiClient referenceMasterDataApiClient(
            @Value("${firefly.security-center.clients.reference-master-data.base-url}") String baseUrl) {
        log.info("Configuring Reference Master Data SDK with base URL: {}", baseUrl);
        com.firefly.common.reference.master.data.sdk.invoker.ApiClient client =
            new com.firefly.common.reference.master.data.sdk.invoker.ApiClient();
        client.setBasePath(baseUrl);
        return client;
    }
    
    @Bean
    public ContractRoleApi contractRoleApi(
            @Qualifier("referenceMasterDataApiClient") 
            com.firefly.common.reference.master.data.sdk.invoker.ApiClient client) {
        return new ContractRoleApi(client);
    }
    
    @Bean
    public ContractRoleScopeApi contractRoleScopeApi(
            @Qualifier("referenceMasterDataApiClient") 
            com.firefly.common.reference.master.data.sdk.invoker.ApiClient client) {
        return new ContractRoleScopeApi(client);
    }
}
