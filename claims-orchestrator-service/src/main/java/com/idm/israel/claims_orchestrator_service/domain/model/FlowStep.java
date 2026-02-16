package com.idm.israel.claims_orchestrator_service.domain.model;

public enum FlowStep {
    CREATE,
    SUBMIT,
    VALIDATE,
    DECIDE,
    APPROVE,
    REJECT,
    PAY,
    CANCEL_CORE
}