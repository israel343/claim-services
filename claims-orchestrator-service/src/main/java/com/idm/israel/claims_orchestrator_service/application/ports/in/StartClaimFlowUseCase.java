package com.idm.israel.claims_orchestrator_service.application.ports.in;

import com.idm.israel.claims_orchestrator_service.application.dto.FlowResponse;
import com.idm.israel.claims_orchestrator_service.application.dto.StartFlowCommand;
import reactor.core.publisher.Mono;

public interface StartClaimFlowUseCase {
    Mono<FlowResponse> start(StartFlowCommand cmd);
}

