package com.firefly.contract.sdk.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ContractResponse {
    private UUID contractId;
    private String contractNumber;
    private String status;
}
