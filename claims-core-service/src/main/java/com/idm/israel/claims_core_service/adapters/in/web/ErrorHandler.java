package com.idm.israel.claims_core_service.adapters.in.web;

import com.idm.israel.claims_core_service.domain.exception.DomainException;
import com.idm.israel.claims_core_service.domain.exception.InvalidTransitionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.time.Instant;

@RestControllerAdvice
public class ErrorHandler {

    record ApiError(String message, String type, Instant timestamp) {}

    @ExceptionHandler(InvalidTransitionException.class)
    public Mono<ResponseEntity<ApiError>> handleInvalidTransition(InvalidTransitionException ex) {
        return Mono.just(
                org.springframework.http.ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(new ApiError(ex.getMessage(), ex.getClass().getSimpleName(), Instant.now()))
        );
    }

    @ExceptionHandler(DomainException.class)
    public Mono<org.springframework.http.ResponseEntity<ApiError>> handleDomain(DomainException ex) {
        return Mono.just(
                org.springframework.http.ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new ApiError(ex.getMessage(), ex.getClass().getSimpleName(), Instant.now()))
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ApiError>> handleIllegalArgument(IllegalArgumentException ex) {
        ex.printStackTrace(); // para ver el stacktrace en consola
        return Mono.just(
                ResponseEntity.badRequest()
                        .body(new ApiError(ex.getMessage(), ex.getClass().getSimpleName(), Instant.now()))
        );
    }

    @ExceptionHandler(Exception.class)
    public Mono<org.springframework.http.ResponseEntity<ApiError>> handleGeneric(Exception ex) {
        return Mono.just(
                org.springframework.http.ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiError("Unexpected error", ex.getClass().getSimpleName(), Instant.now()))
        );
    }


}