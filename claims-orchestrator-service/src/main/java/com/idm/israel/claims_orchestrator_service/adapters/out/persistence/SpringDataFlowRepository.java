package com.idm.israel.claims_orchestrator_service.adapters.out.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface SpringDataFlowRepository extends ReactiveCrudRepository<FlowEntity, UUID> {}
