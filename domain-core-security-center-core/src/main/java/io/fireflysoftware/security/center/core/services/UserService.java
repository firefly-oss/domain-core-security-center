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


package io.fireflysoftware.security.center.core.services;

import org.fireflyframework.security.idp.adapter.IdpAdapter;
import org.fireflyframework.security.idp.dtos.CreateUserRequest;
import org.fireflyframework.security.idp.dtos.CreateUserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Service that delegates user creation to the configured {@link IdpAdapter} and preserves its HTTP status.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final IdpAdapter idpAdapter;

    /**
     * Create a new user in the underlying IDP.
     * Returns the ResponseEntity from the IDP as-is to preserve status codes (e.g., 500 on failure).
     */
    public Mono<ResponseEntity<CreateUserResponse>> createUser(CreateUserRequest request) {
        log.info("Creating user via IDP");
        return idpAdapter.createUser(request)
                .doOnNext(resp -> log.info("User create response status: {}", resp.getStatusCode()))
                .doOnError(err -> log.error("Error creating user via IDP", err));
    }
}
