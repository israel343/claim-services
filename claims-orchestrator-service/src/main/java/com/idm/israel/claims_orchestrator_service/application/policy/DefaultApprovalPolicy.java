package com.idm.israel.claims_orchestrator_service.application.policy;

import com.idm.israel.claims_orchestrator_service.adapters.out.http.CoreDto;

import java.math.BigDecimal;
import java.util.function.Predicate;

public class DefaultApprovalPolicy implements ApprovalPolicy {

    private final BigDecimal autoApproveThreshold;

    public DefaultApprovalPolicy(BigDecimal autoApproveThreshold) {
        this.autoApproveThreshold = autoApproveThreshold;
    }

    @Override
    public Predicate<CoreDto.ClaimResponse> shouldApprove() {
        return claim -> {
            if (claim == null || claim.amountRequested() == null) return false;
            return claim.amountRequested().compareTo(autoApproveThreshold) <= 0;
        };
    }
}
