package com.idm.israel.claims_orchestrator_service.domain.exception;

import java.util.UUID;

public class FlowNotFoundException extends FlowException {
    public FlowNotFoundException(UUID id) {
        super("Flow not found: " + id);
    }
}