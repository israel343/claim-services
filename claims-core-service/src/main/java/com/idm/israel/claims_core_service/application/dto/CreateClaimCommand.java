package com.idm.israel.claims_core_service.application.dto;

import com.idm.israel.claims_core_service.domain.model.ClaimType;

import java.math.BigDecimal;

public record CreateClaimCommand(
        String policyNumber,
        String claimantId,
        ClaimType type,
        String description,
        BigDecimal amountRequested
) {}