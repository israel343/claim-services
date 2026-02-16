package com.idm.israel.claims_core_service.adapters.out.persistance;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface SpringDataClaimRepository extends ReactiveCrudRepository<ClaimEntity, UUID> {
    Flux<ClaimEntity> findByPolicyNumber(String policyNumber);
    Flux<ClaimEntity> findByClaimantId(String claimantId);
}