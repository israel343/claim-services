package com.idm.israel.claims_orchestrator_service.domain.exception;

public class FlowException extends RuntimeException {
    public FlowException(String message) { super(message); }
    public FlowException(String message, Throwable cause) { super(message, cause); }
}