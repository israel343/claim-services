package com.idm.israel.claims_core_service.application.dto;

import com.idm.israel.claims_core_service.domain.model.ClaimStatus;
import com.idm.israel.claims_core_service.domain.model.ClaimType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ClaimResponse(
        UUID id,
        String policyNumber,
        String claimantId,
        ClaimType type,
        String description,
        BigDecimal amountRequested,
        ClaimStatus status,
        Instant createdAt,
        Instant updatedAt
) {}