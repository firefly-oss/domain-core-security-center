/*
 * Copyright 2025 Firefly Software Foundation
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


package io.fireflysoftware.security.center.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Utility class for extracting claims from a JWT without verifying the signature.
 *
 * <p>Note: This parser does NOT validate the JWT signature or headers. It only decodes
 * the payload and reads claims. Use this utility only in contexts where the token has
 * already been authenticated/validated by your security layer (e.g., IDP, gateway),
 * and you just need to read a non-sensitive claim.</p>
 */
public final class JwtClaimUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JwtClaimUtils() {
        // Utility class
    }

    /**
     * Extracts the "userRole" claim from the given JWT token string.
     *
     * @param token a JWT in compact serialization (header.payload.signature)
     * @return the value of the "userRole" claim, or null if the claim is not present
     * @throws IllegalArgumentException if the token format is invalid or cannot be decoded
     */
    public static String extractUserRole(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Empty token");
        }
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Incorrect JWT format");
        }
        try {
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            String userRole = MAPPER.readTree(payload).path("userRole").asText(null);
            return userRole;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token: " + e.getMessage(), e);
        }
    }

}
