package com.idm.israel.claims_core_service.application.service;

import com.idm.israel.claims_core_service.application.dto.ClaimResponse;
import com.idm.israel.claims_core_service.application.ports.in.GetClaimUseCase;
import com.idm.israel.claims_core_service.application.ports.out.ClaimRepositoryPort;
import com.idm.israel.claims_core_service.domain.exception.DomainException;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class GetClaimService implements GetClaimUseCase {

    private final ClaimRepositoryPort repo;

    public GetClaimService(ClaimRepositoryPort repo) {
        this.repo = repo;
    }

    @Override
    public Mono<ClaimResponse> getById(UUID claimId) {
        return repo.findById(claimId)
                .switchIfEmpty(Mono.error(new DomainException("Claim not found: " + claimId)))
                .map(ClaimMapper::toResponse);
    }
}