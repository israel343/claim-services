package com.idm.israel.claims_core_service.application.service;


import com.idm.israel.claims_core_service.application.dto.ClaimResponse;
import com.idm.israel.claims_core_service.application.dto.UpdateClaimCommand;
import com.idm.israel.claims_core_service.application.ports.in.UpdateClaimUseCase;
import com.idm.israel.claims_core_service.application.ports.out.ClaimRepositoryPort;
import com.idm.israel.claims_core_service.domain.exception.DomainException;
import com.idm.israel.claims_core_service.domain.model.ClaimStatus;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

public class UpdateClaimService implements UpdateClaimUseCase {

    private final ClaimRepositoryPort repo;

    public UpdateClaimService(ClaimRepositoryPort repo) {
        this.repo = repo;
    }

    @Override
    public Mono<ClaimResponse> update(UUID claimId, UpdateClaimCommand cmd) {
        Instant now = Instant.now();
        return repo.findById(claimId)
                .switchIfEmpty(Mono.error(new DomainException("Claim not found: " + claimId)))
                .flatMap(existing -> {
                    if (existing.status() != ClaimStatus.DRAFT) {
                        return Mono.error(new DomainException("Claim can only be edited in DRAFT status"));
                    }
                    var updated = existing.withEditableFields(
                            cmd.policyNumber(),
                            cmd.claimantId(),
                            cmd.type(),
                            cmd.description(),
                            cmd.amountRequested(),
                            now
                    );
                    return repo.save(updated);
                })
                .map(ClaimMapper::toResponse);
    }
}