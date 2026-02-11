package com.firefly.customer.sdk.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * Test DTO for Customer Response used in integration test mocks.
 */
@Data
@Builder
public class CustomerResponse {
    private UUID partyId;
    private String firstName;
    private String lastName;
    private String email;
}
