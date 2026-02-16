package com.idm.israel.claims_core_service.application.service;

import com.idm.israel.claims_core_service.application.dto.ClaimResponse;
import com.idm.israel.claims_core_service.application.ports.in.SearchClaimsUseCase;
import com.idm.israel.claims_core_service.application.ports.out.ClaimRepositoryPort;
import com.idm.israel.claims_core_service.domain.model.ClaimStatus;
import com.idm.israel.claims_core_service.domain.model.ClaimType;
import reactor.core.publisher.Flux;

public class SearchClaimsService implements SearchClaimsUseCase {

    private final ClaimRepositoryPort repo;

    public SearchClaimsService(ClaimRepositoryPort repo) {
        this.repo = repo;
    }

    @Override
    public Flux<ClaimResponse> search(ClaimStatus status, String policyNumber, String claimantId, ClaimType type) {
        return repo.search(status, policyNumber, claimantId, type)
               .map(ClaimMapper::toResponse);
    }
}