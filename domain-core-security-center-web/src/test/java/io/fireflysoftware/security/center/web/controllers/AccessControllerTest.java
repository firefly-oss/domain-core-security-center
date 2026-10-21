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

import io.fireflysoftware.security.center.interfaces.access.AccessAction;
import io.fireflysoftware.security.center.interfaces.access.AccessDecision;
import io.fireflysoftware.security.center.interfaces.access.AccessRequest;
import io.fireflysoftware.security.center.interfaces.access.AccessResourceType;
import io.fireflysoftware.security.center.session.AccessPort;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AccessControllerTest {

    @Test
    void returnsThePortDecisionWhenTheActorIsAllowed() {
        AccessPort port = mock(AccessPort.class);
        when(port.decide(any())).thenReturn(Mono.just(AccessDecision.allowed(AccessDecision.SANDBOX_GRANT)));

        WebTestClient client = WebTestClient.bindToController(new AccessController(port)).build();

        client.post()
                .uri("/api/v1/access/checks")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "actorId": "11111111-1111-4111-8111-111111111111",
                          "action": "READ",
                          "resourceType": "ACCOUNT",
                          "resourceId": "10000000-0000-4000-8000-000000000002"
                        }
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.allowed").isEqualTo(true)
                .jsonPath("$.reasonCode").isEqualTo("SANDBOX_GRANT");

        verify(port).decide(any());
    }

    @Test
    void returnsThePortDecisionWhenTheActorIsDenied() {
        AccessPort port = mock(AccessPort.class);
        when(port.decide(any())).thenReturn(Mono.just(AccessDecision.denied(AccessDecision.ACTOR_NOT_ENTITLED)));

        WebTestClient client = WebTestClient.bindToController(new AccessController(port)).build();

        client.post()
                .uri("/api/v1/access/checks")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "actorId": "22222222-2222-4222-8222-222222222222",
                          "action": "CREATE",
                          "resourceType": "PARTY",
                          "resourceId": "10000000-0000-4000-8000-000000000001"
                        }
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.allowed").isEqualTo(false)
                .jsonPath("$.reasonCode").isEqualTo("ACTOR_NOT_ENTITLED");
    }

    @Test
    void requestRequiresActorActionResourceTypeAndResourceId() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        AccessRequest missingActor = AccessRequest.builder()
                .action(AccessAction.READ)
                .resourceType(AccessResourceType.ACCOUNT)
                .resourceId("10000000-0000-4000-8000-000000000002")
                .build();

        assertThat(validator.validate(missingActor)).isNotEmpty();
        assertThat(validator.validate(new AccessRequest())).isNotEmpty();
    }
}
