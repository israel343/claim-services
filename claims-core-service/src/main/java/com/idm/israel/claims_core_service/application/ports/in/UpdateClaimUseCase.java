package com.idm.israel.claims_core_service.application.ports.in;

import com.idm.israel.claims_core_service.application.dto.ClaimResponse;
import com.idm.israel.claims_core_service.application.dto.UpdateClaimCommand;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UpdateClaimUseCase {
    Mono<ClaimResponse> update(UUID claimId, UpdateClaimCommand cmd);
}