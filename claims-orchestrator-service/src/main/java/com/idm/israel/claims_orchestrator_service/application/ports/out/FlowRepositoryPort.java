package com.idm.israel.claims_orchestrator_service.application.ports.out;

import com.idm.israel.claims_orchestrator_service.domain.model.Flow;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FlowRepositoryPort {
    Mono<Flow> save(Flow flow);
    Mono<Flow> findById(UUID flowId);
}