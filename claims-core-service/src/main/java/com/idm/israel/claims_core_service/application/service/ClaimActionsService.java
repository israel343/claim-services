package com.idm.israel.claims_core_service.application.service;

import com.idm.israel.claims_core_service.application.dto.ClaimResponse;
import com.idm.israel.claims_core_service.application.dto.PayClaimCommand;
import com.idm.israel.claims_core_service.application.dto.RejectClaimCommand;
import com.idm.israel.claims_core_service.application.ports.in.ClaimActionsUseCase;
import com.idm.israel.claims_core_service.application.ports.out.ClaimRepositoryPort;
import com.idm.israel.claims_core_service.domain.exception.DomainException;
import com.idm.israel.claims_core_service.domain.model.Claim;
import com.idm.israel.claims_core_service.domain.model.ClaimStatus;
import com.idm.israel.claims_core_service.domain.rules.ClaimTransitions;
import com.idm.israel.claims_core_service.domain.rules.ClaimValidator;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

public class ClaimActionsService implements ClaimActionsUseCase {

    private final ClaimRepositoryPort repo;

    public ClaimActionsService(ClaimRepositoryPort repo) {
        this.repo = repo;
    }

    @Override
    public Mono<ClaimResponse> submit(UUID claimId) {
        return load(claimId)
                .map(c -> {
                    ClaimValidator.validateForSubmit(c);
                    ClaimTransitions.assertCanTransition(c.status(), ClaimStatus.SUBMITTED);
                    return c.withStatus(ClaimStatus.SUBMITTED, Instant.now());
                })
                .flatMap(repo::save)
                .map(ClaimMapper::toResponse);
    }

    @Override
    public Mono<ClaimResponse> validate(UUID claimId) {
        return load(claimId)
                .map(c -> {
                    ClaimValidator.validateForSubmit(c);
                    ClaimTransitions.assertCanTransition(c.status(), ClaimStatus.VALIDATED);
                    return c.withStatus(ClaimStatus.VALIDATED, Instant.now());
                })
                .flatMap(repo::save)
                .map(ClaimMapper::toResponse);
    }

    @Override
    public Mono<ClaimResponse> approve(UUID claimId) {
        return load(claimId)
                .map(c -> {
                    ClaimValidator.validateForApproval(c);
                    ClaimTransitions.assertCanTransition(c.status(), ClaimStatus.APPROVED);
                    return c.withStatus(ClaimStatus.APPROVED, Instant.now());
                })
                .flatMap(repo::save)
                .map(ClaimMapper::toResponse);
    }

    @Override
    public Mono<ClaimResponse> reject(UUID claimId, RejectClaimCommand cmd) {
        return load(claimId)
                .map(c -> {
                    if (cmd == null || cmd.reason() == null || cmd.reason().trim().isEmpty()) {
                        throw new DomainException("reject reason is required");
                    }
                    ClaimTransitions.assertCanTransition(c.status(), ClaimStatus.REJECTED);
                    return c.withStatus(ClaimStatus.REJECTED, Instant.now());
                })
                .flatMap(repo::save)
                .map(ClaimMapper::toResponse);
    }

    @Override
    public Mono<ClaimResponse> pay(UUID claimId, PayClaimCommand cmd) {
        return load(claimId)
                .map(c -> {
                    if (cmd == null || cmd.paymentRef() == null || cmd.paymentRef().trim().isEmpty()) {
                        throw new DomainException("paymentRef is required");
                    }
                    ClaimTransitions.assertCanTransition(c.status(), ClaimStatus.PAID);
                    return c.withStatus(ClaimStatus.PAID, Instant.now());
                })
                .flatMap(repo::save)
                .map(ClaimMapper::toResponse);
    }

    @Override
    public Mono<ClaimResponse> cancel(UUID claimId, String reason) {
        return load(claimId)
                .map(c -> {
                    if (c.status() == ClaimStatus.REJECTED || c.status() == ClaimStatus.PAID || c.status() == ClaimStatus.CANCELLED) {
                        throw new DomainException("Cannot cancel a final claim status: " + c.status());
                    }
                    ClaimTransitions.assertCanTransition(c.status(), ClaimStatus.CANCELLED);
                    return c.withStatus(ClaimStatus.CANCELLED, Instant.now());
                })
                .flatMap(repo::save)
                .map(ClaimMapper::toResponse);
    }

    private Mono<Claim> load(UUID id) {
        return repo.findById(id)
                .switchIfEmpty(Mono.error(new DomainException("Claim not found: " + id)));
    }
}