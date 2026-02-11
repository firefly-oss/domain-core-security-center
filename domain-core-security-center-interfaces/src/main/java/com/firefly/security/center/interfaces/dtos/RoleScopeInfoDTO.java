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
 * Role scope (permission) defining what actions can be performed on what resources
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleScopeInfoDTO {

    @JsonProperty("scopeId")
    private UUID scopeId;

    @JsonProperty("roleId")
    private UUID roleId;

    @JsonProperty("scopeCode")
    private String scopeCode;

    @JsonProperty("scopeName")
    private String scopeName;

    @JsonProperty("description")
    private String description;

    /**
     * Type of action (e.g., READ, WRITE, DELETE, EXECUTE, APPROVE)
     */
    @JsonProperty("actionType")
    private String actionType;

    /**
     * Type of resource (e.g., PRODUCT, TRANSACTION, ACCOUNT, BALANCE)
     */
    @JsonProperty("resourceType")
    private String resourceType;

    @JsonProperty("isActive")
    private Boolean isActive;
}
