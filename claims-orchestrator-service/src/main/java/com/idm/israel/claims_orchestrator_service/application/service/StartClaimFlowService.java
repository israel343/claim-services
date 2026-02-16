package com.idm.israel.claims_orchestrator_service.application.service;

import com.idm.israel.claims_orchestrator_service.application.dto.FlowResponse;
import com.idm.israel.claims_orchestrator_service.application.dto.StartFlowCommand;
import com.idm.israel.claims_orchestrator_service.application.ports.in.StartClaimFlowUseCase;
import reactor.core.publisher.Mono;

public class StartClaimFlowService implements StartClaimFlowUseCase {

    private final FlowEngine engine;

    public StartClaimFlowService(FlowEngine engine) {
        this.engine = engine;
    }

    @Override
    public Mono<FlowResponse> start(StartFlowCommand cmd) {
        return engine.startNewFlow(cmd).map(FlowMapper::toFlowResponse);
    }
}