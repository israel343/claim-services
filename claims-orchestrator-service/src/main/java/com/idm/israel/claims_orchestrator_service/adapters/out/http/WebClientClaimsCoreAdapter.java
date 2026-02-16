package com.idm.israel.claims_orchestrator_service.adapters.out.http;

import com.idm.israel.claims_orchestrator_service.application.ports.out.ClaimsCorePort;
import com.idm.israel.claims_orchestrator_service.domain.exception.CoreCommunicationException;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class WebClientClaimsCoreAdapter implements ClaimsCorePort {

    private final WebClient client;

    public WebClientClaimsCoreAdapter(WebClient client) {
        this.client = client;
    }

    @Override
    public Mono<CoreDto.ClaimResponse> createClaim(CoreDto.CreateClaimRequest req) {
        return client.post()
                .uri("/api/v1/claims")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .retrieve()
                .bodyToMono(CoreDto.ClaimResponse.class)
                .onErrorMap(ex -> new CoreCommunicationException("Core createClaim failed", ex));
    }

    @Override
    public Mono<CoreDto.ClaimResponse> submit(UUID claimId) {
        return postNoBody("/api/v1/claims/" + claimId + "/submit", "submit");
    }

    @Override
    public Mono<CoreDto.ClaimResponse> validate(UUID claimId) {
        return postNoBody("/api/v1/claims/" + claimId + "/validate", "validate");
    }

    @Override
    public Mono<CoreDto.ClaimResponse> approve(UUID claimId) {
        return postNoBody("/api/v1/claims/" + claimId + "/approve", "approve");
    }

    @Override
    public Mono<CoreDto.ClaimResponse> reject(UUID claimId, CoreDto.RejectClaimRequest req) {
        return client.post()
                .uri("/api/v1/claims/{id}/reject", claimId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .retrieve()
                .bodyToMono(CoreDto.ClaimResponse.class)
                .onErrorMap(ex -> new CoreCommunicationException("Core reject failed", ex));
    }

    @Override
    public Mono<CoreDto.ClaimResponse> pay(UUID claimId, CoreDto.PayClaimRequest req) {
        return client.post()
                .uri("/api/v1/claims/{id}/pay", claimId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .retrieve()
                .bodyToMono(CoreDto.ClaimResponse.class)
                .onErrorMap(ex -> new CoreCommunicationException("Core pay failed", ex));
    }

    @Override
    public Mono<CoreDto.ClaimResponse> cancel(UUID claimId, String reason) {
        String r = reason == null ? "" : reason;
        return client.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/claims/{id}/cancel")
                        .queryParam("reason", r)
                        .build(claimId))
                .retrieve()
                .bodyToMono(CoreDto.ClaimResponse.class)
                .onErrorMap(ex -> new CoreCommunicationException("Core cancel failed", ex));
    }

    @Override
    public Mono<CoreDto.ClaimResponse> getClaim(UUID claimId) {
        return client.get()
                .uri("/api/v1/claims/{id}", claimId)
                .retrieve()
                .bodyToMono(CoreDto.ClaimResponse.class)
                .onErrorMap(ex -> new CoreCommunicationException("Core getClaim failed", ex));
    }

    private Mono<CoreDto.ClaimResponse> postNoBody(String path, String op) {
        return client.post()
                .uri(path)
                .retrieve()
                .bodyToMono(CoreDto.ClaimResponse.class)
                .onErrorMap(ex -> new CoreCommunicationException("Core " + op + " failed", ex));
    }
}