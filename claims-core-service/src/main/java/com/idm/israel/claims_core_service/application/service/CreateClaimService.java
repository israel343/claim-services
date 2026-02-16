package com.idm.israel.claims_core_service.application.service;

import com.idm.israel.claims_core_service.application.dto.ClaimResponse;
import com.idm.israel.claims_core_service.application.dto.CreateClaimCommand;
import com.idm.israel.claims_core_service.application.ports.in.CreateClaimUseCase;
import com.idm.israel.claims_core_service.application.ports.out.ClaimRepositoryPort;
import com.idm.israel.claims_core_service.domain.model.Claim;
import com.idm.israel.claims_core_service.domain.model.ClaimStatus;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

public class CreateClaimService implements CreateClaimUseCase {

    private final ClaimRepositoryPort repo;

    public CreateClaimService(ClaimRepositoryPort repo) {
        this.repo = repo;
    }

    @Override
    public Mono<ClaimResponse> create(CreateClaimCommand cmd) {
        Instant now = Instant.now();
        Claim claim = new Claim(
                null,
                cmd.policyNumber(),
                cmd.claimantId(),
                cmd.type(),
                cmd.description(),
                cmd.amountRequested(),
                ClaimStatus.DRAFT,
                now,
                now
        );

        return repo.save(claim).map(ClaimMapper::toResponse);
    }
}