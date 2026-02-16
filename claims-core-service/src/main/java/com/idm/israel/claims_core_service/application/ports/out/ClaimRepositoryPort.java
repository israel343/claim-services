package com.idm.israel.claims_core_service.application.ports.out;

import com.idm.israel.claims_core_service.application.dto.ClaimResponse;
import com.idm.israel.claims_core_service.domain.model.Claim;
import com.idm.israel.claims_core_service.domain.model.ClaimStatus;
import com.idm.israel.claims_core_service.domain.model.ClaimType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ClaimRepositoryPort {
    Mono<Claim> save(Claim claim);
    Mono<Claim> findById(UUID id);
    Flux<Claim> search(ClaimStatus status, String policyNumber, String claimantId, ClaimType type);
    Mono<Void> deleteById(UUID id);
}