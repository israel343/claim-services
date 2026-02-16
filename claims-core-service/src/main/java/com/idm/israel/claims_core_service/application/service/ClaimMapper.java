package com.idm.israel.claims_core_service.application.service;

import com.idm.israel.claims_core_service.application.dto.ClaimResponse;
import com.idm.israel.claims_core_service.domain.model.Claim;

final class ClaimMapper {
    private ClaimMapper() {}

    static ClaimResponse toResponse(Claim c) {
        return new ClaimResponse(
                c.id(),
                c.policyNumber(),
                c.claimantId(),
                c.type(),
                c.description(),
                c.amountRequested(),
                c.status(),
                c.createdAt(),
                c.updatedAt()
        );
    }
}