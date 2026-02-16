package com.idm.israel.claims_orchestrator_service.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record StartFlowCommand(
        @Schema(maxLength = 64)
        String policyNumber,
        @Schema(maxLength = 64)
        String claimantId,
        String type,
        @Schema(maxLength = 500)
        String description,
        BigDecimal amountRequested,
        boolean autoPay
) {}