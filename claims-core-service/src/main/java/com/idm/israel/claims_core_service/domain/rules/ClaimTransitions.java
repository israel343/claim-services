package com.idm.israel.claims_core_service.domain.rules;

import com.idm.israel.claims_core_service.domain.exception.InvalidTransitionException;
import com.idm.israel.claims_core_service.domain.model.ClaimStatus;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;

public final class ClaimTransitions {
    private static final EnumMap<ClaimStatus, Set<ClaimStatus>> ALLOWED = new EnumMap<>(ClaimStatus.class);

    static {
        ALLOWED.put(ClaimStatus.DRAFT, EnumSet.of(ClaimStatus.SUBMITTED, ClaimStatus.CANCELLED));
        ALLOWED.put(ClaimStatus.SUBMITTED, EnumSet.of(ClaimStatus.VALIDATED, ClaimStatus.CANCELLED));
        ALLOWED.put(ClaimStatus.VALIDATED, EnumSet.of(ClaimStatus.APPROVED, ClaimStatus.REJECTED, ClaimStatus.CANCELLED));
        ALLOWED.put(ClaimStatus.APPROVED, EnumSet.of(ClaimStatus.PAID, ClaimStatus.CANCELLED));
        ALLOWED.put(ClaimStatus.REJECTED, EnumSet.noneOf(ClaimStatus.class));
        ALLOWED.put(ClaimStatus.PAID, EnumSet.noneOf(ClaimStatus.class));
        ALLOWED.put(ClaimStatus.CANCELLED, EnumSet.noneOf(ClaimStatus.class));
    }

    private ClaimTransitions() {}

    public static void assertCanTransition(ClaimStatus from, ClaimStatus to) {
        Set<ClaimStatus> allowedTargets = ALLOWED.getOrDefault(from, EnumSet.noneOf(ClaimStatus.class));
        if (!allowedTargets.contains(to)) {
            throw new InvalidTransitionException(from, to);
        }
    }
}
