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

import io.fireflysoftware.security.center.interfaces.identity.Subject;
import io.fireflysoftware.security.center.session.IdentityPort;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IdentityControllerTest {

    @Test
    void returnsTheSubjectFromThePort() {
        IdentityPort port = mock(IdentityPort.class);
        when(port.authenticate(any())).thenReturn(Mono.just(Subject.builder()
                .actorId("11111111-1111-4111-8111-111111111111")
                .provider("sandbox")
                .roles(List.of())
                .build()));

        WebTestClient.bindToController(new IdentityController(port)).build()
                .post()
                .uri("/api/v1/identity/subjects")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"username\":\"sample-journey\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.actorId").isEqualTo("11111111-1111-4111-8111-111111111111")
                .jsonPath("$.provider").isEqualTo("sandbox")
                .jsonPath("$.roles").isEmpty();
    }

    @Test
    void unknownSubjectIsNotFound() {
        IdentityPort port = mock(IdentityPort.class);
        when(port.authenticate(any())).thenReturn(Mono.empty());

        WebTestClient.bindToController(new IdentityController(port)).build()
                .post()
                .uri("/api/v1/identity/subjects")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"username\":\"someone-else\"}")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void missingUsernameAndActorIdIsBadRequest() {
        IdentityPort port = mock(IdentityPort.class);

        WebTestClient.bindToController(new IdentityController(port)).build()
                .post()
                .uri("/api/v1/identity/subjects")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isBadRequest();
    }
}
