package com.idm.israel.claims_orchestrator_service.application;

import com.idm.israel.claims_orchestrator_service.application.dto.StartFlowCommand;
import com.idm.israel.claims_orchestrator_service.application.service.FlowEngine;
import com.idm.israel.claims_orchestrator_service.application.service.StartClaimFlowService;
import com.idm.israel.claims_orchestrator_service.domain.model.Flow;
import com.idm.israel.claims_orchestrator_service.domain.model.FlowStatus;
import com.idm.israel.claims_orchestrator_service.domain.model.FlowStep;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

class StartClaimFlowServiceTest {

    @Test
    void start_delegatesToEngine_andMapsResponse() {
        FlowEngine engine = mock(FlowEngine.class);
        StartClaimFlowService service = new StartClaimFlowService(engine);

        UUID flowId = UUID.randomUUID();
        UUID claimId = UUID.randomUUID();
        Instant now = Instant.now();

        Flow flow = new Flow(
                flowId,
                claimId,
                FlowStatus.COMPLETED,
                FlowStep.PAY,
                List.of(FlowStep.CREATE, FlowStep.SUBMIT, FlowStep.VALIDATE, FlowStep.DECIDE, FlowStep.APPROVE, FlowStep.PAY),
                null,
                now,
                now
        );

        StartFlowCommand cmd = new StartFlowCommand("POL-1","DNI-1","COLLISION","desc", BigDecimal.TEN, true);
        when(engine.startNewFlow(cmd)).thenReturn(Mono.just(flow));

        StepVerifier.create(service.start(cmd))
                .assertNext(resp -> {
                    org.junit.jupiter.api.Assertions.assertEquals(flowId, resp.flowId());
                    org.junit.jupiter.api.Assertions.assertEquals(claimId, resp.claimId());
                    org.junit.jupiter.api.Assertions.assertEquals(FlowStatus.COMPLETED, resp.status());
                })
                .verifyComplete();

        verify(engine).startNewFlow(cmd);
    }
}
