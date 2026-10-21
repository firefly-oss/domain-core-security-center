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

package io.fireflysoftware.security.center.interfaces.identity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * IdentityPort response. {@code actorId} is the subject AccessPort evaluates.
 *
 * <pre>
 * {
 *   "actorId": "11111111-1111-4111-8111-111111111111",
 *   "provider": "sandbox",
 *   "roles": []
 * }
 * </pre>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Subject {

    @JsonProperty("actorId")
    private String actorId;

    /** sandbox, keycloak, cognito, or internal-db. JSON uses that property token. */
    @JsonProperty("provider")
    private IdentityAdapter provider;

    /** Realm roles from the identity provider. Empty when the provider has none. */
    @JsonProperty("roles")
    @Builder.Default
    private List<String> roles = new ArrayList<>();
}
