package com.idm.israel.claims_core_service.adapters.in.web;

import com.idm.israel.claims_core_service.application.dto.ClaimResponse;
import com.idm.israel.claims_core_service.application.dto.CreateClaimCommand;
import com.idm.israel.claims_core_service.application.ports.in.*;
import com.idm.israel.claims_core_service.domain.model.ClaimStatus;
import com.idm.israel.claims_core_service.domain.model.ClaimType;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ClaimControllerWebTest {

    @Test
    void postCreate_returns200() {
        CreateClaimUseCase createUC = mock(CreateClaimUseCase.class);
        GetClaimUseCase getUC = mock(GetClaimUseCase.class);
        SearchClaimsUseCase searchUC = mock(SearchClaimsUseCase.class);
        UpdateClaimUseCase updateUC = mock(UpdateClaimUseCase.class);
        ClaimActionsUseCase actionsUC = mock(ClaimActionsUseCase.class);

        ClaimController controller = new ClaimController(createUC, getUC, searchUC, updateUC, actionsUC);
        ErrorHandler errorHandler = new ErrorHandler();

        WebTestClient client = WebTestClient
                .bindToController(controller)
                .controllerAdvice(errorHandler)
                .build();

        UUID id = UUID.randomUUID();
        when(createUC.create(any())).thenReturn(Mono.just(
                new ClaimResponse(
                        id, "POL-1", "DNI-1", ClaimType.COLLISION, "desc",
                        BigDecimal.valueOf(10), ClaimStatus.DRAFT, Instant.now(), Instant.now()
                )
        ));

        client.post()
                .uri("/api/v1/claims")
                .bodyValue(new CreateClaimCommand("POL-1","DNI-1",ClaimType.COLLISION,"desc",BigDecimal.valueOf(10)))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(id.toString())
                .jsonPath("$.status").isEqualTo("DRAFT");
    }

    @Test
    void getAll_returnsList() {
        CreateClaimUseCase createUC = mock(CreateClaimUseCase.class);
        GetClaimUseCase getUC = mock(GetClaimUseCase.class);
        SearchClaimsUseCase searchUC = mock(SearchClaimsUseCase.class);
        UpdateClaimUseCase updateUC = mock(UpdateClaimUseCase.class);
        ClaimActionsUseCase actionsUC = mock(ClaimActionsUseCase.class);

        ClaimController controller = new ClaimController(createUC, getUC, searchUC, updateUC, actionsUC);

        WebTestClient client = WebTestClient.bindToController(controller).build();

        when(searchUC.search(null, null, null, null)).thenReturn(Flux.empty());

        client.get()
                .uri("/api/v1/claims")
                .exchange()
                .expectStatus().isOk();
    }
}
