package com.idm.israel.claims_core_service.application.service;

import com.idm.israel.claims_core_service.application.dto.CreateClaimCommand;
import com.idm.israel.claims_core_service.application.ports.out.ClaimRepositoryPort;
import com.idm.israel.claims_core_service.domain.model.Claim;
import com.idm.israel.claims_core_service.domain.model.ClaimStatus;
import com.idm.israel.claims_core_service.domain.model.ClaimType;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateClaimServiceTest {

    @Test
    void create_setsDraft_andSaves() {
        ClaimRepositoryPort repo = mock(ClaimRepositoryPort.class);
        CreateClaimService service = new CreateClaimService(repo);

        ArgumentCaptor<Claim> captor = ArgumentCaptor.forClass(Claim.class);
        when(repo.save(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        CreateClaimCommand cmd = new CreateClaimCommand(
                "POL-1", "DNI-1", ClaimType.COLLISION, "desc", BigDecimal.valueOf(10)
        );

        StepVerifier.create(service.create(cmd))
                .assertNext(resp -> {
                    assertThat(resp.status()).isEqualTo(ClaimStatus.DRAFT);
                    assertThat(resp.policyNumber()).isEqualTo("POL-1");
                })
                .verifyComplete();

        verify(repo).save(captor.capture());
        assertThat(captor.getValue().status()).isEqualTo(ClaimStatus.DRAFT);
    }
}
