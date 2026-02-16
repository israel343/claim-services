package com.idm.israel.claims_orchestrator_service.application.service;

import com.idm.israel.claims_orchestrator_service.application.dto.FlowStatusResponse;
import com.idm.israel.claims_orchestrator_service.application.ports.in.GetFlowStatusUseCase;
import com.idm.israel.claims_orchestrator_service.application.ports.out.FlowRepositoryPort;
import com.idm.israel.claims_orchestrator_service.domain.exception.FlowNotFoundException;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class GetFlowStatusService implements GetFlowStatusUseCase {

    private final FlowRepositoryPort repo;

    public GetFlowStatusService(FlowRepositoryPort repo) {
        this.repo = repo;
    }

    @Override
    public Mono<FlowStatusResponse> get(UUID flowId) {
        return repo.findById(flowId)
                .switchIfEmpty(Mono.error(new FlowNotFoundException(flowId)))
                .map(FlowMapper::toStatusResponse);
    }
}