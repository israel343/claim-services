package com.idm.israel.claims_orchestrator_service.application.service;

import com.idm.israel.claims_orchestrator_service.application.dto.FlowResponse;
import com.idm.israel.claims_orchestrator_service.application.dto.FlowStatusResponse;
import com.idm.israel.claims_orchestrator_service.application.dto.RetryFlowResponse;
import com.idm.israel.claims_orchestrator_service.domain.model.Flow;

final class FlowMapper {
    private FlowMapper() {}

    static FlowResponse toFlowResponse(Flow f) {
        return new FlowResponse(f.id(), f.claimId(), f.status(), f.lastStep(), f.executedSteps(), f.errorMessage(), f.createdAt(), f.updatedAt());
    }

    static FlowStatusResponse toStatusResponse(Flow f) {
        return new FlowStatusResponse(f.id(), f.claimId(), f.status(), f.lastStep(), f.executedSteps(), f.errorMessage(), f.createdAt(), f.updatedAt());
    }

    static RetryFlowResponse toRetryResponse(Flow f) {
        return new RetryFlowResponse(f.id(), f.claimId(), f.status(), f.lastStep(), f.executedSteps(), f.errorMessage(), f.createdAt(), f.updatedAt());
    }
}
