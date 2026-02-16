package com.idm.israel.claims_core_service.application.ports.in;

import java.util.UUID;

import com.idm.israel.claims_core_service.application.dto.ClaimResponse;
import reactor.core.publisher.Mono;

public interface GetClaimUseCase {
    Mono<ClaimResponse> getById(UUID claimId);
}
