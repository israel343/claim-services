package com.idm.israel.claims_core_service.domain.model;

import java.time.Instant;
import java.util.UUID;

public record ClaimEvent(
        UUID id,
        UUID claimId,
        String eventType,
        String details,
        Instant occurredAt
) {}
