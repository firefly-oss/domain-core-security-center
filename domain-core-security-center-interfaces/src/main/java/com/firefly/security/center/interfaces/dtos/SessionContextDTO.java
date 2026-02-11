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

package com.firefly.security.center.interfaces.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Complete session context containing customer information, active contracts,
 * products, roles, and permissions for authorization decisions
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class SessionContextDTO {

    /**
     * Unique session identifier
     */
    @JsonProperty("sessionId")
    private String sessionId;

    /**
     * Party ID from X-Party-Id header
     */
    @JsonProperty("partyId")
    private UUID partyId;

    /**
     * Customer/party information
     */
    @JsonProperty("customerInfo")
    private CustomerInfoDTO customerInfo;

    /**
     * All active contracts for this party with their associated products and roles
     */
    @JsonProperty("activeContracts")
    private List<ContractInfoDTO> activeContracts;

    /**
     * Session creation timestamp
     */
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    /**
     * Last access timestamp
     */
    @JsonProperty("lastAccessedAt")
    private LocalDateTime lastAccessedAt;

    /**
     * Session expiration timestamp
     */
    @JsonProperty("expiresAt")
    private LocalDateTime expiresAt;

    /**
     * Client IP address
     */
    @JsonProperty("ipAddress")
    private String ipAddress;

    /**
     * User agent string
     */
    @JsonProperty("userAgent")
    private String userAgent;

    /**
     * Session status
     */
    @JsonProperty("status")
    private SessionStatus status;

    /**
     * Additional session metadata
     */
    @JsonProperty("metadata")
    private SessionMetadataDTO metadata;

    /**
     * Session status enumeration
     */
    public enum SessionStatus {
        ACTIVE,
        EXPIRED,
        INVALIDATED,
        LOCKED
    }
}
