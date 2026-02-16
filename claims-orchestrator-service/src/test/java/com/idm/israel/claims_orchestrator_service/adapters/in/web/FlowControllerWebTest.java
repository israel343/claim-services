package com.idm.israel.claims_orchestrator_service.adapters.in.web;

import com.idm.israel.claims_orchestrator_service.adapters.in.ErrorHandler;
import com.idm.israel.claims_orchestrator_service.adapters.in.FlowController;
import com.idm.israel.claims_orchestrator_service.application.dto.FlowResponse;
import com.idm.israel.claims_orchestrator_service.application.dto.FlowStatusResponse;
import com.idm.israel.claims_orchestrator_service.application.dto.RetryFlowResponse;
import com.idm.israel.claims_orchestrator_service.application.dto.StartFlowCommand;
import com.idm.israel.claims_orchestrator_service.application.ports.in.GetFlowStatusUseCase;
import com.idm.israel.claims_orchestrator_service.application.ports.in.RetryFlowUseCase;
import com.idm.israel.claims_orchestrator_service.application.ports.in.StartClaimFlowUseCase;
import com.idm.israel.claims_orchestrator_service.domain.model.FlowStatus;
import com.idm.israel.claims_orchestrator_service.domain.model.FlowStep;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FlowControllerWebTest {

    @Test
    void postStartFlow_returnsFlowResponse() {
        StartClaimFlowUseCase startUC = mock(StartClaimFlowUseCase.class);
        GetFlowStatusUseCase statusUC = mock(GetFlowStatusUseCase.class);
        RetryFlowUseCase retryUC = mock(RetryFlowUseCase.class);

        FlowController controller = new FlowController(startUC, statusUC, retryUC);
        ErrorHandler errorHandler = new ErrorHandler();

        WebTestClient client = WebTestClient
                .bindToController(controller)
                .controllerAdvice(errorHandler)
                .build();

        UUID flowId = UUID.randomUUID();
        UUID claimId = UUID.randomUUID();
        Instant now = Instant.now();

        when(startUC.start(any())).thenReturn(Mono.just(
                new FlowResponse(flowId, claimId, FlowStatus.COMPLETED, FlowStep.PAY,
                        List.of(FlowStep.CREATE, FlowStep.SUBMIT, FlowStep.VALIDATE, FlowStep.DECIDE, FlowStep.APPROVE, FlowStep.PAY),
                        null, now, now)
        ));

        client.post()
                .uri("/api/v1/flows/claims")
                .bodyValue(new StartFlowCommand("POL-1", "DNI-1", "COLLISION", "desc", BigDecimal.TEN, true))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.flowId").isEqualTo(flowId.toString())
                .jsonPath("$.claimId").isEqualTo(claimId.toString())
                .jsonPath("$.status").isEqualTo("COMPLETED");
    }

    @Test
    void getStatus_returnsFlowStatus() {
        StartClaimFlowUseCase startUC = mock(StartClaimFlowUseCase.class);
        GetFlowStatusUseCase statusUC = mock(GetFlowStatusUseCase.class);
        RetryFlowUseCase retryUC = mock(RetryFlowUseCase.class);

        FlowController controller = new FlowController(startUC, statusUC, retryUC);

        WebTestClient client = WebTestClient.bindToController(controller).build();

        UUID flowId = UUID.randomUUID();
        UUID claimId = UUID.randomUUID();
        Instant now = Instant.now();

        when(statusUC.get(flowId)).thenReturn(Mono.just(
                new FlowStatusResponse(flowId, claimId, FlowStatus.RUNNING, FlowStep.SUBMIT,
                        List.of(FlowStep.CREATE, FlowStep.SUBMIT), null, now, now)
        ));

        client.get()
                .uri("/api/v1/flows/{flowId}", flowId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.flowId").isEqualTo(flowId.toString())
                .jsonPath("$.status").isEqualTo("RUNNING");
    }

    @Test
    void retry_returnsRetryResponse() {
        StartClaimFlowUseCase startUC = mock(StartClaimFlowUseCase.class);
        GetFlowStatusUseCase statusUC = mock(GetFlowStatusUseCase.class);
        RetryFlowUseCase retryUC = mock(RetryFlowUseCase.class);

        FlowController controller = new FlowController(startUC, statusUC, retryUC);

        WebTestClient client = WebTestClient.bindToController(controller).build();

        UUID flowId = UUID.randomUUID();
        UUID claimId = UUID.randomUUID();
        Instant now = Instant.now();

        when(retryUC.retry(flowId)).thenReturn(Mono.just(
                new RetryFlowResponse(flowId, claimId, FlowStatus.COMPLETED, FlowStep.VALIDATE,
                        List.of(FlowStep.SUBMIT, FlowStep.VALIDATE), null, now, now)
        ));

        client.post()
                .uri("/api/v1/flows/{flowId}/retry", flowId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.flowId").isEqualTo(flowId.toString())
                .jsonPath("$.status").isEqualTo("COMPLETED");
    }
}
