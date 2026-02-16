package com.idm.israel.claims_core_service.domain.rules;

import com.idm.israel.claims_core_service.domain.exception.InvalidTransitionException;
import com.idm.israel.claims_core_service.domain.model.ClaimStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ClaimTransitionsTest {

    @Test
    void allowsDraftToSubmitted() {
        assertDoesNotThrow(() ->
                ClaimTransitions.assertCanTransition(ClaimStatus.DRAFT, ClaimStatus.SUBMITTED)
        );
    }

    @Test
    void rejectsDraftToPaid() {
        assertThrows(InvalidTransitionException.class, () ->
                ClaimTransitions.assertCanTransition(ClaimStatus.DRAFT, ClaimStatus.PAID)
        );
    }
}
