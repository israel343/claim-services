package com.idm.israel.claims_core_service.domain.rules;

import com.idm.israel.claims_core_service.domain.exception.DomainException;
import com.idm.israel.claims_core_service.domain.model.Claim;
import com.idm.israel.claims_core_service.domain.model.ClaimStatus;
import com.idm.israel.claims_core_service.domain.model.ClaimType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ClaimValidatorTest {

    @Test
    void validateForSubmit_ok() {
        Claim c = new Claim(
                UUID.randomUUID(), "POL-1", "DNI-1", ClaimType.COLLISION,
                "desc", BigDecimal.valueOf(10), ClaimStatus.DRAFT,
                Instant.now(), Instant.now()
        );

        assertDoesNotThrow(() -> ClaimValidator.validateForSubmit(c));
    }

    @Test
    void validateForSubmit_missingPolicy_throws() {
        Claim c = new Claim(
                UUID.randomUUID(), "  ", "DNI-1", ClaimType.COLLISION,
                "desc", BigDecimal.valueOf(10), ClaimStatus.DRAFT,
                Instant.now(), Instant.now()
        );

        assertThrows(DomainException.class, () -> ClaimValidator.validateForSubmit(c));
    }
}
