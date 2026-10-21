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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

/**
 * Closed selection for {@code firefly.security-center.identity.adapter}.
 * Property files and JSON keep the tokens {@code sandbox}, {@code keycloak},
 * {@code cognito}, and {@code internal-db}.
 */
public enum IdentityAdapter {
    SANDBOX("sandbox"),
    KEYCLOAK("keycloak"),
    COGNITO("cognito"),
    INTERNAL_DB("internal-db");

    private final String propertyValue;

    IdentityAdapter(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    @JsonValue
    public String propertyValue() {
        return propertyValue;
    }

    @JsonCreator
    public static IdentityAdapter fromProperty(String value) {
        if (value == null || value.isBlank()) {
            return SANDBOX;
        }
        String token = value.trim().toLowerCase(Locale.ROOT).replace('_', '-');
        for (IdentityAdapter adapter : values()) {
            if (adapter.propertyValue.equals(token)) {
                return adapter;
            }
        }
        throw new IllegalArgumentException("Unknown identity adapter: " + value);
    }
}
