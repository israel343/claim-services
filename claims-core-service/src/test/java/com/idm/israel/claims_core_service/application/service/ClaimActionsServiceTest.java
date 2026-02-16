package com.idm.israel.claims_core_service.application.service;

import com.idm.israel.claims_core_service.application.ports.out.ClaimRepositoryPort;
import com.idm.israel.claims_core_service.core.testutil.ClaimFixtures;
import com.idm.israel.claims_core_service.domain.exception.DomainException;
import com.idm.israel.claims_core_service.domain.model.Claim;
import com.idm.israel.claims_core_service.domain.model.ClaimStatus;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ClaimActionsServiceTest {

    @Test
    void submit_movesDraftToSubmitted() {
        ClaimRepositoryPort repo = mock(ClaimRepositoryPort.class);
        ClaimActionsService service = new ClaimActionsService(repo);

        Claim draft = ClaimFixtures.draft();
        UUID id = draft.id();

        when(repo.findById(id)).thenReturn(Mono.just(draft));
        when(repo.save(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(service.submit(id))
                .assertNext(resp -> {
                    org.junit.jupiter.api.Assertions.assertEquals(ClaimStatus.SUBMITTED, resp.status());
                })
                .verifyComplete();

        verify(repo).findById(id);
        verify(repo).save(any());
    }

    @Test
    void submit_missingClaim_throws() {
        ClaimRepositoryPort repo = mock(ClaimRepositoryPort.class);
        ClaimActionsService service = new ClaimActionsService(repo);

        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(service.submit(id))
                .expectError(DomainException.class)
                .verify();
    }
}