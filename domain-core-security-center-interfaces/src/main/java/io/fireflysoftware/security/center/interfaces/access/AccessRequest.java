/*
 * Copyright 2026 Firefly Software Foundation
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

package io.fireflysoftware.security.center.interfaces.access;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AccessPort request. Field names and enum values are the contract.
 *
 * <pre>
 * {
 *   "actorId": "11111111-1111-4111-8111-111111111111",
 *   "action": "READ",
 *   "resourceType": "ACCOUNT",
 *   "resourceId": "10000000-0000-4000-8000-000000000002"
 * }
 * </pre>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccessRequest {

    @NotBlank
    @JsonProperty("actorId")
    private String actorId;

    @NotNull
    @JsonProperty("action")
    private AccessAction action;

    @NotNull
    @JsonProperty("resourceType")
    private AccessResourceType resourceType;

    @NotBlank
    @JsonProperty("resourceId")
    private String resourceId;
}
