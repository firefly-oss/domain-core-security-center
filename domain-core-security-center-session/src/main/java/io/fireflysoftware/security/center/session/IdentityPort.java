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

package io.fireflysoftware.security.center.session;

import io.fireflysoftware.security.center.interfaces.identity.IdentityRequest;
import io.fireflysoftware.security.center.interfaces.identity.Subject;
import reactor.core.publisher.Mono;

/**
 * Decides who the actor is.
 *
 * <p>Experience journeys call this port. They do not import an identity-provider
 * client. The reference adapter delegates to Jafly {@code IdpAdapter}
 * ({@code org.fireflyframework:fireflyframework-security-idp-keycloak} when the
 * selected provider is Keycloak). The sandbox adapter returns the sample subject
 * with no identity-provider process. A deployment selects the adapter with
 * {@code firefly.security-center.identity.adapter}.</p>
 *
 * @see AccessPort
 */
public interface IdentityPort {

    /**
     * Authenticates a username and returns the subject.
     * Empty when the username is not recognized.
     */
    Mono<Subject> authenticate(IdentityRequest request);

    /**
     * Resolves an actor id to the subject AccessPort will evaluate.
     * Empty when this adapter does not know that actor.
     */
    Mono<Subject> resolve(String actorId);
}
