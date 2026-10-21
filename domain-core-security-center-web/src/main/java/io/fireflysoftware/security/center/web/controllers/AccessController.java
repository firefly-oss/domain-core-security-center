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

import io.fireflysoftware.security.center.interfaces.access.AccessDecision;
import io.fireflysoftware.security.center.interfaces.access.AccessRequest;
import io.fireflysoftware.security.center.session.AccessPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Access check for experience journeys. The decision comes from {@link AccessPort}.
 * This controller does not allow or deny a request on its own.
 */
@RestController
@RequestMapping("/api/v1/access")
@RequiredArgsConstructor
public class AccessController {

    private final AccessPort accessPort;

    /**
     * Decides whether the actor may perform the action on the resource.
     * Both allowed and denied outcomes return HTTP 200. The body says which.
     */
    @PostMapping("/checks")
    public Mono<ResponseEntity<AccessDecision>> decideAccess(@Valid @RequestBody AccessRequest request) {
        return accessPort.decide(request).map(ResponseEntity::ok);
    }
}
