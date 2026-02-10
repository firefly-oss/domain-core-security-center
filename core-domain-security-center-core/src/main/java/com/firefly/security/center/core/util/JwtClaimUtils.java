package com.firefly.security.center.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.fireflyframework.idp.dtos.enums.UserRoleEnum;

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
     * @return the value of the "userRole" claim as UserRoleEnum, or null if the claim is not present
     * @throws IllegalArgumentException if the token format is invalid or cannot be decoded
     */
    public static UserRoleEnum extractUserRole(String token) {
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
            return userRole != null ? UserRoleEnum.valueOf(userRole) : null;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token: " + e.getMessage(), e);
        }
    }

}
