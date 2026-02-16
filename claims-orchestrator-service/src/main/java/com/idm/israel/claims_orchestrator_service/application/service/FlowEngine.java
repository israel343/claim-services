package com.idm.israel.claims_orchestrator_service.application.service;

import com.idm.israel.claims_orchestrator_service.adapters.out.http.CoreDto;
import com.idm.israel.claims_orchestrator_service.application.dto.StartFlowCommand;
import com.idm.israel.claims_orchestrator_service.application.policy.ApprovalPolicy;
import com.idm.israel.claims_orchestrator_service.application.ports.out.ClaimsCorePort;
import com.idm.israel.claims_orchestrator_service.application.ports.out.FlowRepositoryPort;
import com.idm.israel.claims_orchestrator_service.domain.exception.CoreCommunicationException;
import com.idm.israel.claims_orchestrator_service.domain.model.Flow;
import com.idm.israel.claims_orchestrator_service.domain.model.FlowStatus;
import com.idm.israel.claims_orchestrator_service.domain.model.FlowStep;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class FlowEngine {

    private final ClaimsCorePort core;
    private final FlowRepositoryPort flowRepo;
    private final ApprovalPolicy approvalPolicy;

    public FlowEngine(ClaimsCorePort core, FlowRepositoryPort flowRepo, ApprovalPolicy approvalPolicy) {
        this.core = core;
        this.flowRepo = flowRepo;
        this.approvalPolicy = approvalPolicy;
    }

    public Mono<Flow> startNewFlow(StartFlowCommand cmd) {
        Instant now = Instant.now();
        UUID flowId = UUID.randomUUID();

        Flow initial = new Flow(
                null,
                null,
                FlowStatus.RUNNING,
                null,
                List.of(),
                null,
                now,
                now
        );

        return flowRepo.save(initial)
                .flatMap(flow -> executeFrom(flow, cmd, FlowStep.CREATE));
    }

    public Mono<Flow> retryFlow(Flow existing) {

        if (existing.status() != FlowStatus.FAILED && existing.status() != FlowStatus.COMPENSATED) {
            return Mono.error(new IllegalStateException(
                    "Retry not allowed. Flow status must be FAILED or COMPENSATED. Current=" + existing.status()
            ));
        }

        FlowStep next = nextStep(existing.lastStep());
        if (next == null) {
            return Mono.just(existing); // nada que reintentar
        }

        if (next == FlowStep.CREATE) {
            return Mono.error(new IllegalStateException(
                    "Cannot retry CREATE without original payload. Persist StartFlowCommand in FlowEntity if you want retries for CREATE."
            ));
        }

        return executeFrom(existing, null, next);
    }

    private Mono<Flow> executeFrom(Flow flow, StartFlowCommand cmd, FlowStep startAt) {
        List<FlowStep> executed = new ArrayList<>(flow.executedSteps());
        Supplier<Instant> now = Instant::now;

        AtomicReference<Flow> latest = new AtomicReference<>(flow);

        Mono<Flow> pipeline = Mono.just(flow);

        if (startAt == FlowStep.CREATE) {
            pipeline = pipeline.flatMap(f ->
                    step(latest, f, executed, FlowStep.CREATE, () ->
                            core.createClaim(new CoreDto.CreateClaimRequest(
                                    cmd.policyNumber(),
                                    cmd.claimantId(),
                                    cmd.type(),
                                    cmd.description(),
                                    cmd.amountRequested()
                            ))
                    )
                            .flatMap(created -> {
                                Flow withClaim = f.withClaimId(created.id(), now.get());
                                latest.set(withClaim);
                                return flowRepo.save(withClaim)
                                        .doOnNext(latest::set);
                            })
            );
        }

        pipeline = pipeline.flatMap(f -> {
            if (f.claimId() == null) return Mono.error(new IllegalStateException("claimId is null"));
            return Mono.just(f);
        });

        if (shouldRunFrom(startAt, FlowStep.SUBMIT)) {
            pipeline = pipeline.flatMap(f ->
                    step(latest, f, executed, FlowStep.SUBMIT, () -> core.submit(f.claimId()))
                            .thenReturn(f)
            );
        }

        if (shouldRunFrom(startAt, FlowStep.VALIDATE)) {
            pipeline = pipeline.flatMap(f ->
                    step(latest, f, executed, FlowStep.VALIDATE, () -> core.validate(f.claimId()))
                            .thenReturn(f)
            );
        }

        pipeline = pipeline.flatMap(f ->
                step(latest, f, executed, FlowStep.DECIDE, () -> core.getClaim(f.claimId()))
                        .flatMap(claim -> {
                            boolean approve = approvalPolicy.shouldApprove().test(claim);
                            return Mono.just(new Decision(approve, claim));
                        })
                        .thenReturn(f)
        );

        pipeline = pipeline.flatMap(f ->
                core.getClaim(f.claimId())
                        .flatMap(claim -> {
                            boolean approve = approvalPolicy.shouldApprove().test(claim);
                            if (approve) {
                                if (shouldRunFrom(startAt, FlowStep.APPROVE)) {
                                    return step(latest, f, executed, FlowStep.APPROVE, () -> core.approve(f.claimId()))
                                            .thenReturn(f);
                                }
                                return Mono.just(f);
                            } else {
                                if (shouldRunFrom(startAt, FlowStep.REJECT)) {
                                    return step(latest, f, executed, FlowStep.REJECT, () ->
                                            core.reject(f.claimId(), new CoreDto.RejectClaimRequest("Policy rule: requires manual review"))
                                    ).thenReturn(f);
                                }
                                return Mono.just(f);
                            }
                        })
        );

        pipeline = pipeline.flatMap(f -> {
            if (cmd != null && cmd.autoPay()) {
                if (shouldRunFrom(startAt, FlowStep.PAY)) {
                    return step(latest, f, executed, FlowStep.PAY,
                            () -> core.pay(f.claimId(), new CoreDto.PayClaimRequest("PAY-" + f.claimId()))
                    ).thenReturn(f);
                }
            }
            return Mono.just(f);
        });

        return pipeline
                .flatMap(f -> {
                    Flow completed = f.withUpdate(
                            FlowStatus.COMPLETED,
                            lastOrNull(executed),
                            List.copyOf(executed),
                            null,
                            now.get()
                    );
                    latest.set(completed);
                    return flowRepo.save(completed).doOnNext(latest::set);
                })
                .onErrorResume(ex -> compensate(latest.get(), executed, ex));
    }

    private Mono<Flow> compensate(Flow flow, List<FlowStep> executed, Throwable ex) {
        Instant now = Instant.now();
        String msg = ex.getMessage();

        Mono<Void> cancelAttempt = (flow.claimId() == null)
                ? Mono.empty()
                : step(new AtomicReference<>(flow), flow, executed, FlowStep.CANCEL_CORE,
                () -> core.cancel(flow.claimId(), "Compensation after error: " + safe(msg)))
                .then();

        return cancelAttempt
                .onErrorResume(ignore -> Mono.empty())
                .then(Mono.defer(() -> {
                    Flow failed = flow.withUpdate(
                            flow.claimId() != null ? FlowStatus.COMPENSATED : FlowStatus.FAILED,
                            lastOrNull(executed),
                            List.copyOf(executed),
                            safe(msg),
                            now
                    );
                    return flowRepo.save(failed);
                }))
                .onErrorMap(e -> new CoreCommunicationException("Compensation failed", e));
    }

    private <T> Mono<T> step(AtomicReference<Flow> latest, Flow flow, List<FlowStep> executed, FlowStep step, Supplier<Mono<T>> call) {
        Instant now = Instant.now();
        Flow updated = flow.withUpdate(FlowStatus.RUNNING, step, append(executed, step), null, now);
        latest.set(updated);
        return flowRepo.save(updated)
                .doOnNext(latest::set)
                .then(call.get());
    }

    private static List<FlowStep> append(List<FlowStep> list, FlowStep step) {
        if (!list.isEmpty() && list.get(list.size() - 1) == step) return list;
        list.add(step);
        return list;
    }

    private static FlowStep lastOrNull(List<FlowStep> list) {
        return list.isEmpty() ? null : list.get(list.size() - 1);
    }

    private static boolean shouldRunFrom(FlowStep startAt, FlowStep step) {
        if (startAt == null) return true;
        return step.ordinal() >= startAt.ordinal();
    }

    private static String safe(String s) {
        return s == null ? "Unknown error" : s;
    }

    private static FlowStep nextStep(FlowStep last) {
        if (last == null) return FlowStep.CREATE;
        return switch (last) {
            case CREATE -> FlowStep.SUBMIT;
            case SUBMIT -> FlowStep.VALIDATE;
            case VALIDATE -> FlowStep.DECIDE;
            case DECIDE -> FlowStep.APPROVE;
            case APPROVE -> FlowStep.PAY;
            case REJECT, PAY, CANCEL_CORE -> null;
        };
    }

    private record Decision(boolean approve, CoreDto.ClaimResponse claim) {}
}