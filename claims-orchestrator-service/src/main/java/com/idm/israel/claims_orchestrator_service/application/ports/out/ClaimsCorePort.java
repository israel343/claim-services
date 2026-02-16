package com.idm.israel.claims_orchestrator_service.application.ports.out;

import com.idm.israel.claims_orchestrator_service.adapters.out.http.CoreDto;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ClaimsCorePort {
    Mono<CoreDto.ClaimResponse> createClaim(CoreDto.CreateClaimRequest req);
    Mono<CoreDto.ClaimResponse> submit(UUID claimId);
    Mono<CoreDto.ClaimResponse> validate(UUID claimId);
    Mono<CoreDto.ClaimResponse> approve(UUID claimId);
    Mono<CoreDto.ClaimResponse> reject(UUID claimId, CoreDto.RejectClaimRequest req);
    Mono<CoreDto.ClaimResponse> pay(UUID claimId, CoreDto.PayClaimRequest req);
    Mono<CoreDto.ClaimResponse> cancel(UUID claimId, String reason);
    Mono<CoreDto.ClaimResponse> getClaim(UUID claimId);
}