package com.idm.israel.claims_orchestrator_service.adapters.out.persistence;

import com.idm.israel.claims_orchestrator_service.domain.model.Flow;
import com.idm.israel.claims_orchestrator_service.domain.model.FlowStep;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

final class FlowPersistenceMapper {
    private FlowPersistenceMapper() {}

    static FlowEntity toEntity(Flow f) {
        FlowEntity e = new FlowEntity();
        e.setId(f.id());
        e.setClaimId(f.claimId());
        e.setStatus(f.status());
        e.setLastStep(f.lastStep());
        e.setExecutedSteps(toCsv(f.executedSteps()));
        e.setErrorMessage(f.errorMessage());
        e.setCreatedAt(toLdt(f.createdAt()));
        e.setUpdatedAt(toLdt(f.updatedAt()));
        return e;
    }

    static Flow toDomain(FlowEntity e) {
        return new Flow(
                e.getId(),
                e.getClaimId(),
                e.getStatus(),
                e.getLastStep(),
                fromCsv(e.getExecutedSteps()),
                e.getErrorMessage(),
                toInstant(e.getCreatedAt()),
                toInstant(e.getUpdatedAt())
        );
    }

    private static String toCsv(List<FlowStep> steps) {
        if (steps == null || steps.isEmpty()) return "";
        return steps.stream().map(Enum::name).collect(Collectors.joining(","));
    }

    private static List<FlowStep> fromCsv(String csv) {
        if (csv == null || csv.isBlank()) return List.of();
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(FlowStep::valueOf)
                .toList();
    }

    private static LocalDateTime toLdt(Instant i) {
        return i == null ? null : LocalDateTime.ofInstant(i, ZoneOffset.UTC);
    }

    private static Instant toInstant(LocalDateTime ldt) {
        return ldt == null ? null : ldt.toInstant(ZoneOffset.UTC);
    }
}