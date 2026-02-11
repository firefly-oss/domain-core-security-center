package com.firefly.contract.sdk.client;

import com.firefly.contract.sdk.dto.ContractResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

/**
 * Test wrapper interface for Contract Management SDK.
 * Used for mocking in integration tests.
 */
public interface ContractManagementClient {
    Mono<List<ContractResponse>> getContractsByPartyId(UUID partyId);
}
