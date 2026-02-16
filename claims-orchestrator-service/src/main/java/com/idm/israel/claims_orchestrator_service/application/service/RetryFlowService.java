package com.idm.israel.claims_orchestrator_service.application.service;

import com.idm.israel.claims_orchestrator_service.application.dto.RetryFlowResponse;
import com.idm.israel.claims_orchestrator_service.application.ports.in.RetryFlowUseCase;
import com.idm.israel.claims_orchestrator_service.application.ports.out.FlowRepositoryPort;
import com.idm.israel.claims_orchestrator_service.domain.exception.FlowNotFoundException;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class RetryFlowService implements RetryFlowUseCase {

    private final FlowRepositoryPort repo;
    private final FlowEngine engine;

    public RetryFlowService(FlowRepositoryPort repo, FlowEngine engine) {
        this.repo = repo;
        this.engine = engine;
    }

    @Override
    public Mono<RetryFlowResponse> retry(UUID flowId) {
        return repo.findById(flowId)
                .switchIfEmpty(Mono.error(new FlowNotFoundException(flowId)))
                .flatMap(engine::retryFlow)
                .map(FlowMapper::toRetryResponse);
    }
}