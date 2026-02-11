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

import java.util.UUID;

/**
 * Customer/party basic information for session context
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerInfoDTO {

    @JsonProperty("partyId")
    private UUID partyId;

    @JsonProperty("partyKind")
    private String partyKind; // NATURAL_PERSON or LEGAL_ENTITY

    @JsonProperty("tenantId")
    private UUID tenantId;

    @JsonProperty("fullName")
    private String fullName;

    @JsonProperty("preferredLanguage")
    private String preferredLanguage;

    @JsonProperty("email")
    private String email;

    @JsonProperty("phoneNumber")
    private String phoneNumber;

    @JsonProperty("taxIdNumber")
    private String taxIdNumber;

    @JsonProperty("isActive")
    private Boolean isActive;
}
