package com.idm.israel.claims_core_service.core.testutil;

import com.idm.israel.claims_core_service.domain.model.Claim;
import com.idm.israel.claims_core_service.domain.model.ClaimStatus;
import com.idm.israel.claims_core_service.domain.model.ClaimType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public final class ClaimFixtures {
    private ClaimFixtures(){}

    public static Claim draft() {
        Instant now = Instant.now();
        return new Claim(
                UUID.randomUUID(),
                "POL-1",
                "DNI-1",
                ClaimType.COLLISION,
                "desc",
                BigDecimal.valueOf(100),
                ClaimStatus.DRAFT,
                now,
                now
        );
    }
}