package com.idm.israel.claims_core_service.adapters.out.persistance;

import com.idm.israel.claims_core_service.domain.model.Claim;

final class ClaimPersistenceMapper {
    private ClaimPersistenceMapper() {}

    static ClaimEntity toEntity(Claim c) {
        ClaimEntity e = new ClaimEntity();
        e.setId(c.id());
        e.setPolicyNumber(c.policyNumber());
        e.setClaimantId(c.claimantId());
        e.setType(c.type());
        e.setDescription(c.description());
        e.setAmountRequested(c.amountRequested());
        e.setStatus(c.status());
        e.setCreatedAt(c.createdAt());
        e.setUpdatedAt(c.updatedAt());
        return e;
    }

    static Claim toDomain(ClaimEntity e) {
        return new Claim(
                e.getId(),
                e.getPolicyNumber(),
                e.getClaimantId(),
                e.getType(),
                e.getDescription(),
                e.getAmountRequested(),
                e.getStatus(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}