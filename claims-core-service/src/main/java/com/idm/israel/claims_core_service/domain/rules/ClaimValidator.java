package com.idm.israel.claims_core_service.domain.rules;


import com.idm.israel.claims_core_service.domain.exception.DomainException;
import com.idm.israel.claims_core_service.domain.model.Claim;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Predicate;

public final class ClaimValidator {
    private ClaimValidator() {}

    private static final Predicate<String> NOT_BLANK = s -> s != null && !s.trim().isEmpty();

    public static void validateForSubmit(Claim c) {
        require(NOT_BLANK.test(c.policyNumber()), "policyNumber is required");
        require(NOT_BLANK.test(c.claimantId()), "claimantId is required");
        require(c.type() != null, "type is required");
        require(NOT_BLANK.test(c.description()), "description is required");
        require(validAmount(c.amountRequested()), "amountRequested must be > 0");
    }

    public static void validateForApproval(Claim c) {
        validateForSubmit(c);
    }

    private static boolean validAmount(BigDecimal v) {
        return Optional.ofNullable(v).map(a -> a.compareTo(BigDecimal.ZERO) > 0).orElse(false);
    }

    private static void require(boolean condition, String msg) {
        if (!condition) throw new DomainException(msg);
    }
}