package com.idm.israel.claims_core_service.application.ports.in;
import com.idm.israel.claims_core_service.application.dto.ClaimResponse;
import com.idm.israel.claims_core_service.application.dto.CreateClaimCommand;
import reactor.core.publisher.Mono;

public interface CreateClaimUseCase {
    Mono<ClaimResponse> create(CreateClaimCommand cmd);
}
