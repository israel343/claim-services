package com.idm.israel.claims_orchestrator_service.application.dto;

import com.idm.israel.claims_orchestrator_service.domain.model.FlowStatus;
import com.idm.israel.claims_orchestrator_service.domain.model.FlowStep;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record RetryFlowResponse(
        UUID flowId,
        UUID claimId,
        FlowStatus status,
        FlowStep lastStep,
        List<FlowStep> executedSteps,
        String errorMessage,
        Instant createdAt,
        Instant updatedAt
) {}