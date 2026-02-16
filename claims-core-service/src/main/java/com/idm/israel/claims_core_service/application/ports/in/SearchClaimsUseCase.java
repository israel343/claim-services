package com.idm.israel.claims_core_service.application.ports.in;

import com.idm.israel.claims_core_service.application.dto.ClaimResponse;
import com.idm.israel.claims_core_service.domain.model.ClaimStatus;
import com.idm.israel.claims_core_service.domain.model.ClaimType;
import reactor.core.publisher.Flux;

public interface SearchClaimsUseCase {
    Flux<ClaimResponse> search(ClaimStatus status, String policyNumber, String claimantId, ClaimType type);
}
