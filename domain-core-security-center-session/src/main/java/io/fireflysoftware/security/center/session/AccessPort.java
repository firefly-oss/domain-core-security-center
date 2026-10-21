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

import io.fireflysoftware.security.center.interfaces.access.AccessDecision;
import io.fireflysoftware.security.center.interfaces.access.AccessRequest;
import reactor.core.publisher.Mono;

/**
 * Decides whether an actor may access a resource.
 *
 * <p>Callers, including the access controller, depend on this port. They do not
 * decide allow or deny themselves. The shipped adapter reads the sandbox policy
 * file and does not call an identity provider. A later adapter can implement the
 * same port against an entitlement store, or the session-grant adapter can
 * evaluate role scopes already held on the party session.</p>
 *
 * @see FireflySessionManager
 */
public interface AccessPort {

    /**
     * Decides the request.
     *
     * @param request actor, action, resource type, and resource id
     * @return allowed or denied, with a reason code
     */
    Mono<AccessDecision> decide(AccessRequest request);
}
