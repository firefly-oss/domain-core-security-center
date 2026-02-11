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

package com.firefly.security.center.web.controllers;

import com.firefly.security.center.interfaces.dtos.SessionContextDTO;
import com.firefly.security.center.session.FireflySessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * REST controller for session management operations
 */
@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
@Slf4j
public class SessionController {

    private final FireflySessionManager sessionManager;

    /**
     * Creates or retrieves session from X-Party-Id header
     */
    @PostMapping
    public Mono<ResponseEntity<SessionContextDTO>> createOrGetSession(ServerWebExchange exchange) {
        log.info("Creating or retrieving session");
        
        return sessionManager.createOrGetSession(exchange)
                .map(ResponseEntity::ok)
                .onErrorResume(IllegalArgumentException.class, 
                    error -> Mono.just(ResponseEntity.badRequest().build()));
    }

    /**
     * Gets session by sessionId
     */
    @GetMapping("/{sessionId}")
    public Mono<ResponseEntity<SessionContextDTO>> getSessionById(@PathVariable String sessionId) {
        log.info("Retrieving session by sessionId: {}", sessionId);
        
        return sessionManager.getSessionBySessionId(sessionId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Gets session by partyId
     */
    @GetMapping("/party/{partyId}")
    public Mono<ResponseEntity<SessionContextDTO>> getSessionByPartyId(@PathVariable UUID partyId) {
        log.info("Retrieving session by partyId: {}", partyId);
        
        return sessionManager.getSessionByPartyId(partyId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Invalidates a session
     */
    @DeleteMapping("/{sessionId}")
    public Mono<ResponseEntity<Void>> invalidateSession(@PathVariable String sessionId) {
        log.info("Invalidating session: {}", sessionId);
        
        return sessionManager.invalidateSession(sessionId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }

    /**
     * Invalidates all sessions for a party
     */
    @DeleteMapping("/party/{partyId}")
    public Mono<ResponseEntity<Void>> invalidateSessionsByPartyId(@PathVariable UUID partyId) {
        log.info("Invalidating all sessions for partyId: {}", partyId);
        
        return sessionManager.invalidateSessionsByPartyId(partyId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }

    /**
     * Refreshes a session
     */
    @PostMapping("/{sessionId}/refresh")
    public Mono<ResponseEntity<SessionContextDTO>> refreshSession(@PathVariable String sessionId) {
        log.info("Refreshing session: {}", sessionId);
        
        return sessionManager.refreshSession(sessionId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Validates a session
     */
    @GetMapping("/{sessionId}/validate")
    public Mono<ResponseEntity<Boolean>> validateSession(@PathVariable String sessionId) {
        log.info("Validating session: {}", sessionId);
        
        return sessionManager.getSessionBySessionId(sessionId)
                .flatMap(sessionManager::isSessionValid)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.ok(false));
    }

    /**
     * Checks if party has access to product
     */
    @GetMapping("/access-check")
    public Mono<ResponseEntity<Boolean>> checkAccess(
            @RequestParam UUID partyId,
            @RequestParam UUID productId) {
        log.info("Checking access for partyId: {} to productId: {}", partyId, productId);
        
        return sessionManager.hasAccessToProduct(partyId, productId)
                .map(ResponseEntity::ok);
    }

    /**
     * Checks if party has specific permission on product
     */
    @GetMapping("/permission-check")
    public Mono<ResponseEntity<Boolean>> checkPermission(
            @RequestParam UUID partyId,
            @RequestParam UUID productId,
            @RequestParam String actionType,
            @RequestParam(required = false) String resourceType) {
        log.info("Checking permission for partyId: {} on productId: {} - action: {}, resource: {}",
                partyId, productId, actionType, resourceType);
        
        return sessionManager.hasPermission(partyId, productId, actionType, resourceType)
                .map(ResponseEntity::ok);
    }
}
