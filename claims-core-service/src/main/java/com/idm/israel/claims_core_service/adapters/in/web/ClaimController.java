package com.idm.israel.claims_core_service.adapters.in.web;

import com.idm.israel.claims_core_service.application.dto.*;
import com.idm.israel.claims_core_service.application.ports.in.*;
import com.idm.israel.claims_core_service.domain.model.ClaimStatus;
import com.idm.israel.claims_core_service.domain.model.ClaimType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/claims")
public class ClaimController {

    private final CreateClaimUseCase createUC;
    private final GetClaimUseCase getUC;
    private final SearchClaimsUseCase searchUC;
    private final UpdateClaimUseCase updateUC;
    private final ClaimActionsUseCase actionsUC;

    public ClaimController(
            CreateClaimUseCase createUC,
            GetClaimUseCase getUC,
            SearchClaimsUseCase searchUC,
            UpdateClaimUseCase updateUC,
            ClaimActionsUseCase actionsUC
    ) {
        this.createUC = createUC;
        this.getUC = getUC;
        this.searchUC = searchUC;
        this.updateUC = updateUC;
        this.actionsUC = actionsUC;
    }

    @PostMapping
    public Mono<ClaimResponse> create(@RequestBody CreateClaimCommand cmd) {
        return createUC.create(cmd);
    }

    @GetMapping("/{claimId}")
    public Mono<ClaimResponse> getById(@PathVariable UUID claimId) {
        return getUC.getById(claimId);
    }

    @GetMapping
    public Flux<ClaimResponse> search(
            @RequestParam(required = false) ClaimStatus status,
            @RequestParam(required = false) String policyNumber,
            @RequestParam(required = false) String claimantId,
            @RequestParam(required = false) ClaimType type
    ) {
        return searchUC.search(status, policyNumber, claimantId, type);
    }

    @PutMapping("/{claimId}")
    public Mono<ClaimResponse> update(@PathVariable UUID claimId, @RequestBody UpdateClaimCommand cmd) {
        return updateUC.update(claimId, cmd);
    }

    @PostMapping("/{claimId}/submit")
    public Mono<ClaimResponse> submit(@PathVariable UUID claimId) {
        return actionsUC.submit(claimId);
    }

    @PostMapping("/{claimId}/validate")
    public Mono<ClaimResponse> validate(@PathVariable UUID claimId) {
        return actionsUC.validate(claimId);
    }

    @PostMapping("/{claimId}/approve")
    public Mono<ClaimResponse> approve(@PathVariable UUID claimId) {
        return actionsUC.approve(claimId);
    }

    @PostMapping("/{claimId}/reject")
    public Mono<ClaimResponse> reject(@PathVariable UUID claimId, @RequestBody RejectClaimCommand cmd) {
        return actionsUC.reject(claimId, cmd);
    }

    @PostMapping("/{claimId}/pay")
    public Mono<ClaimResponse> pay(@PathVariable UUID claimId, @RequestBody PayClaimCommand cmd) {
        return actionsUC.pay(claimId, cmd);
    }

    @PostMapping("/{claimId}/cancel")
    public Mono<ClaimResponse> cancel(@PathVariable UUID claimId, @RequestParam(required = false) String reason) {
        return actionsUC.cancel(claimId, reason);
    }
}