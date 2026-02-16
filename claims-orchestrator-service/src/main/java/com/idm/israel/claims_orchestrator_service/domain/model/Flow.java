package com.idm.israel.claims_orchestrator_service.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record Flow(
        UUID id,
        UUID claimId,
        FlowStatus status,
        FlowStep lastStep,
        List<FlowStep> executedSteps,
        String errorMessage,
        Instant createdAt,
        Instant updatedAt
) {
    public Flow withUpdate(FlowStatus status, FlowStep lastStep, List<FlowStep> executedSteps, String errorMessage, Instant now) {
        return new Flow(
                this.id,
                this.claimId,
                status,
                lastStep,
                executedSteps,
                errorMessage,
                this.createdAt,
                now
        );
    }

    public Flow withClaimId(UUID claimId, Instant now) {
        return new Flow(
                this.id,
                claimId,
                this.status,
                this.lastStep,
                this.executedSteps,
                this.errorMessage,
                this.createdAt,
                now
        );
    }
}
