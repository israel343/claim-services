package com.idm.israel.claims_orchestrator_service.adapters.out.http;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public final class CoreDto {

    private CoreDto() {}

    public record CreateClaimRequest(
            String policyNumber,
            String claimantId,
            String type,
            String description,
            BigDecimal amountRequested
    ) {}

    public record RejectClaimRequest(String reason) {}

    public record PayClaimRequest(String paymentRef) {}

    public record ClaimResponse(
            UUID id,
            String policyNumber,
            String claimantId,
            String type,
            String description,
            BigDecimal amountRequested,
            String status,
            Instant createdAt,
            Instant updatedAt
    ) {}
}
