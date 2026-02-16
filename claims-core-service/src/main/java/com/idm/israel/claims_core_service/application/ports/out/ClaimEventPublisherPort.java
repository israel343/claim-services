package com.idm.israel.claims_core_service.application.ports.out;

import com.idm.israel.claims_core_service.domain.model.ClaimEvent;
import reactor.core.publisher.Mono;

public interface ClaimEventPublisherPort {
    Mono<Void> publish(ClaimEvent event);
}
