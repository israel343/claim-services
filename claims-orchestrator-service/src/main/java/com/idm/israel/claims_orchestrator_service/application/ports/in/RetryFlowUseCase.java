package com.idm.israel.claims_orchestrator_service.application.ports.in;

import com.idm.israel.claims_orchestrator_service.application.dto.RetryFlowResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RetryFlowUseCase {
    Mono<RetryFlowResponse> retry(UUID flowId);
}
