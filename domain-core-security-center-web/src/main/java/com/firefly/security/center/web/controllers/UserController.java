package com.firefly.security.center.web.controllers;

import org.fireflyframework.idp.dtos.CreateUserRequest;
import org.fireflyframework.idp.dtos.CreateUserResponse;
import com.firefly.security.center.core.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * REST controller for user management operations.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Handles the creation of a new user.
     *
     * @param request the details of the user to be created, encapsulated in a {@link CreateUserRequest} object
     * @return a {@link Mono} wrapping a {@link ResponseEntity} object containing the {@link CreateUserResponse},
     *         or an internal server error response in case of an error
     */
    @PostMapping
    public Mono<ResponseEntity<CreateUserResponse>> createUser(@RequestBody CreateUserRequest request) {
        log.info("Received create user request");
        return userService.createUser(request)
                .onErrorResume(ex -> {
                    log.error("Error creating user", ex);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }
}
