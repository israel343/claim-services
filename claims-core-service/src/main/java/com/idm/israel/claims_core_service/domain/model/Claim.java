package com.idm.israel.claims_core_service.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class Claim {
    private final UUID id;
    private final String policyNumber;
    private final String claimantId;
    private final ClaimType type;
    private final String description;
    private final BigDecimal amountRequested;
    private final ClaimStatus status;
    private final Instant createdAt;
    private final Instant updatedAt;

    public Claim(
            UUID id,
            String policyNumber,
            String claimantId,
            ClaimType type,
            String description,
            BigDecimal amountRequested,
            ClaimStatus status,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.policyNumber = policyNumber;
        this.claimantId = claimantId;
        this.type = type;
        this.description = description;
        this.amountRequested = amountRequested;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID id() { return id; }
    public String policyNumber() { return policyNumber; }
    public String claimantId() { return claimantId; }
    public ClaimType type() { return type; }
    public String description() { return description; }
    public BigDecimal amountRequested() { return amountRequested; }
    public ClaimStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }

    public Claim withStatus(ClaimStatus newStatus, Instant now) {
        return new Claim(
                this.id,
                this.policyNumber,
                this.claimantId,
                this.type,
                this.description,
                this.amountRequested,
                newStatus,
                this.createdAt,
                now
        );
    }

    public Claim withEditableFields(String policyNumber, String claimantId, ClaimType type, String description, BigDecimal amountRequested, Instant now) {
        return new Claim(
                this.id,
                policyNumber,
                claimantId,
                type,
                description,
                amountRequested,
                this.status,
                this.createdAt,
                now
        );
    }

    public void assertIdPresent() {
        Objects.requireNonNull(id, "id is required");
    }
}