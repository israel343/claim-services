package com.idm.israel.claims_orchestrator_service.domain;


import com.idm.israel.claims_orchestrator_service.adapters.out.http.CoreDto;
import com.idm.israel.claims_orchestrator_service.application.dto.StartFlowCommand;
import com.idm.israel.claims_orchestrator_service.application.policy.ApprovalPolicy;
import com.idm.israel.claims_orchestrator_service.application.ports.out.ClaimsCorePort;
import com.idm.israel.claims_orchestrator_service.application.ports.out.FlowRepositoryPort;
import com.idm.israel.claims_orchestrator_service.application.service.FlowEngine;
import com.idm.israel.claims_orchestrator_service.domain.model.FlowStatus;
import com.idm.israel.claims_orchestrator_service.domain.model.FlowStep;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class FlowEngineTest {

    @Test
    void startNewFlow_happyPath_completes() {
        ClaimsCorePort core = mock(ClaimsCorePort.class);
        FlowRepositoryPort flowRepo = mock(FlowRepositoryPort.class);

        ApprovalPolicy policy = () -> (Predicate<CoreDto.ClaimResponse>) claim -> true;

        FlowEngine engine = new FlowEngine(core, flowRepo, policy);

        UUID claimId = UUID.randomUUID();
        CoreDto.ClaimResponse created = new CoreDto.ClaimResponse(
                claimId, "POL-1", "DNI-1", "COLLISION", "desc",
                BigDecimal.valueOf(900), "DRAFT", Instant.now(), Instant.now()
        );

        when(core.createClaim(any())).thenReturn(Mono.just(created));
        when(core.submit(claimId)).thenReturn(Mono.just(created));
        when(core.validate(claimId)).thenReturn(Mono.just(created));
        when(core.getClaim(claimId)).thenReturn(Mono.just(created));
        when(core.approve(claimId)).thenReturn(Mono.just(created));
        when(core.pay(eq(claimId), any())).thenReturn(Mono.just(created));

        when(flowRepo.save(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StartFlowCommand cmd = new StartFlowCommand(
                "POL-1", "DNI-1", "COLLISION", "desc", BigDecimal.valueOf(900), true
        );

        StepVerifier.create(engine.startNewFlow(cmd))
                .assertNext(flow -> {
                    org.junit.jupiter.api.Assertions.assertEquals(claimId, flow.claimId());
                    org.junit.jupiter.api.Assertions.assertEquals(FlowStatus.COMPLETED, flow.status());

                    List<FlowStep> steps = flow.executedSteps();
                    org.junit.jupiter.api.Assertions.assertTrue(steps.contains(FlowStep.CREATE));
                    org.junit.jupiter.api.Assertions.assertTrue(steps.contains(FlowStep.SUBMIT));
                    org.junit.jupiter.api.Assertions.assertTrue(steps.contains(FlowStep.VALIDATE));
                    org.junit.jupiter.api.Assertions.assertTrue(steps.contains(FlowStep.DECIDE));
                    org.junit.jupiter.api.Assertions.assertTrue(steps.contains(FlowStep.APPROVE));
                    org.junit.jupiter.api.Assertions.assertTrue(steps.contains(FlowStep.PAY));
                })
                .verifyComplete();

        verify(core).createClaim(any());
        verify(core).submit(claimId);
        verify(core).validate(claimId);
        verify(core, atLeastOnce()).getClaim(claimId);
        verify(core).approve(claimId);
        verify(core).pay(eq(claimId), any());
        verify(core, never()).cancel(any(), any());
    }

    @Test
    void startNewFlow_onError_compensatesByCancellingCore() {
        ClaimsCorePort core = mock(ClaimsCorePort.class);
        FlowRepositoryPort flowRepo = mock(FlowRepositoryPort.class);

        ApprovalPolicy policy = () -> (Predicate<CoreDto.ClaimResponse>) claim -> true;

        FlowEngine engine = new FlowEngine(core, flowRepo, policy);

        UUID claimId = UUID.randomUUID();
        CoreDto.ClaimResponse created = new CoreDto.ClaimResponse(
                claimId, "POL-1", "DNI-1", "COLLISION", "desc",
                BigDecimal.valueOf(900), "DRAFT", Instant.now(), Instant.now()
        );

        when(core.createClaim(any())).thenReturn(Mono.just(created));
        when(core.submit(claimId)).thenReturn(Mono.error(new RuntimeException("submit failed")));
        when(core.cancel(eq(claimId), any())).thenReturn(Mono.just(created));

        when(flowRepo.save(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StartFlowCommand cmd = new StartFlowCommand(
                "POL-1", "DNI-1", "COLLISION", "desc", BigDecimal.valueOf(900), false
        );

        StepVerifier.create(engine.startNewFlow(cmd))
                .assertNext(flow -> {
                    org.junit.jupiter.api.Assertions.assertEquals(FlowStatus.COMPENSATED, flow.status());
                    org.junit.jupiter.api.Assertions.assertEquals(claimId, flow.claimId());
                    org.junit.jupiter.api.Assertions.assertNotNull(flow.errorMessage());
                })
                .verifyComplete();

        verify(core).createClaim(any());
        verify(core).submit(claimId);
        verify(core).cancel(eq(claimId), any());
    }
}
