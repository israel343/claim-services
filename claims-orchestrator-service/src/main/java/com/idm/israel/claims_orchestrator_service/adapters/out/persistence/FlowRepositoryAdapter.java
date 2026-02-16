package com.idm.israel.claims_orchestrator_service.adapters.out.persistence;

import com.idm.israel.claims_orchestrator_service.application.ports.out.FlowRepositoryPort;
import com.idm.israel.claims_orchestrator_service.domain.model.Flow;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class FlowRepositoryAdapter implements FlowRepositoryPort {

    private final SpringDataFlowRepository repo;

    public FlowRepositoryAdapter(SpringDataFlowRepository repo) {
        this.repo = repo;
    }

    public Mono<Flow> save(Flow flow) {
        FlowEntity e = FlowPersistenceMapper.toEntity(flow);

        boolean firstSave = (flow.lastStep() == null) &&
                (flow.executedSteps() == null || flow.executedSteps().isEmpty());

        if (firstSave) e.markNew(); else e.markNotNew();

        return repo.save(e).map(FlowPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Flow> findById(UUID flowId) {
        return repo.findById(flowId).map(FlowPersistenceMapper::toDomain);
    }
}
