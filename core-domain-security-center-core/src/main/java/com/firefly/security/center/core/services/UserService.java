package com.firefly.security.center.core.services;

import org.fireflyframework.idp.adapter.IdpAdapter;
import org.fireflyframework.idp.dtos.CreateUserRequest;
import org.fireflyframework.idp.dtos.CreateUserResponse;
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
