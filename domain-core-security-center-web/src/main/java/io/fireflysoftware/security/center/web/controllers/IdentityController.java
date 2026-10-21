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

package io.fireflysoftware.security.center.web.controllers;

import io.fireflysoftware.security.center.interfaces.identity.IdentityRequest;
import io.fireflysoftware.security.center.interfaces.identity.Subject;
import io.fireflysoftware.security.center.session.IdentityPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Resolves who the actor is. The controller calls {@link IdentityPort} and does
 * not talk to Keycloak or any other identity provider.
 */
@RestController
@RequestMapping("/api/v1/identity")
@RequiredArgsConstructor
public class IdentityController {

    private final IdentityPort identityPort;

    /**
     * Authenticates a username, or resolves an actor id, and returns the subject.
     * Unknown subjects return HTTP 404. A body with neither field returns HTTP 400.
     */
    @PostMapping("/subjects")
    public Mono<ResponseEntity<Subject>> subject(@RequestBody IdentityRequest request) {
        if (request == null || (isBlank(request.getUsername()) && isBlank(request.getActorId()))) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
        Mono<Subject> subject = isBlank(request.getUsername())
                ? identityPort.resolve(request.getActorId())
                : identityPort.authenticate(request);
        return subject.map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
