package com.idm.israel.claims_orchestrator_service.application.ports.in;

import com.idm.israel.claims_orchestrator_service.application.dto.FlowStatusResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface GetFlowStatusUseCase {
    Mono<FlowStatusResponse> get(UUID flowId);
}