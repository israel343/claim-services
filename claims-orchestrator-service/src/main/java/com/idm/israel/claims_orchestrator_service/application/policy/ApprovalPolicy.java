package com.idm.israel.claims_orchestrator_service.application.policy;

import com.idm.israel.claims_orchestrator_service.adapters.out.http.CoreDto;

import java.util.function.Predicate;

public interface ApprovalPolicy {
    Predicate<CoreDto.ClaimResponse> shouldApprove();
}