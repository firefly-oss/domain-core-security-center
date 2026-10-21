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
import lombok.ToString;

/**
 * IdentityPort request. Supply a username to authenticate, or an actor id to resolve
 * a subject that is already known. The sandbox adapter does not read {@code password}.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IdentityRequest {

    @JsonProperty("username")
    private String username;

    @JsonProperty("actorId")
    private String actorId;

    @ToString.Exclude
    @JsonProperty("password")
    private String password;
}
