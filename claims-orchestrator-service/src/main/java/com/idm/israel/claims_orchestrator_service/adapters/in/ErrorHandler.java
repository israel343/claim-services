package com.idm.israel.claims_orchestrator_service.adapters.in;

import com.idm.israel.claims_orchestrator_service.domain.exception.FlowException;
import com.idm.israel.claims_orchestrator_service.domain.exception.FlowNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import reactor.core.publisher.Mono;

import java.time.Instant;

@RestControllerAdvice
public class ErrorHandler {

    public record ApiError(String message, String type, Instant timestamp) {}

    @ExceptionHandler(FlowNotFoundException.class)
    public Mono<ResponseEntity<ApiError>> handleNotFound(FlowNotFoundException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError(ex.getMessage(), ex.getClass().getSimpleName(), Instant.now())));
    }

    @ExceptionHandler(FlowException.class)
    public Mono<ResponseEntity<ApiError>> handleFlow(FlowException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(ex.getMessage(), ex.getClass().getSimpleName(), Instant.now())));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ApiError>> handleIllegalArg(IllegalArgumentException ex) {
        ex.printStackTrace();
        return Mono.just(ResponseEntity.badRequest()
                .body(new ApiError(ex.getMessage(), ex.getClass().getSimpleName(), Instant.now())));
    }

    @ExceptionHandler(IllegalStateException.class)
    public Mono<ResponseEntity<ApiError>> handleIllegalState(IllegalStateException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiError(ex.getMessage(), ex.getClass().getSimpleName(), Instant.now())));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public Mono<ResponseEntity<ApiError>> handleNoResource(NoResourceFoundException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("Resource not found: " + ex.getReason(), ex.getClass().getSimpleName(), Instant.now())));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ApiError>> handleGeneric(Exception ex) {
        ex.printStackTrace();
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError("Unexpected error", ex.getClass().getSimpleName(), Instant.now())));
    }
}