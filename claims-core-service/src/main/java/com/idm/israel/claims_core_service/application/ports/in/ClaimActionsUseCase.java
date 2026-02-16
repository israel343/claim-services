package com.idm.israel.claims_core_service.application.ports.in;


import com.idm.israel.claims_core_service.application.dto.ClaimResponse;
import com.idm.israel.claims_core_service.application.dto.PayClaimCommand;
import com.idm.israel.claims_core_service.application.dto.RejectClaimCommand;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ClaimActionsUseCase {
    Mono<ClaimResponse> submit(UUID claimId);
    Mono<ClaimResponse> validate(UUID claimId);
    Mono<ClaimResponse> approve(UUID claimId);
    Mono<ClaimResponse> reject(UUID claimId, RejectClaimCommand cmd);
    Mono<ClaimResponse> pay(UUID claimId, PayClaimCommand cmd);
    Mono<ClaimResponse> cancel(UUID claimId, String reason);
}