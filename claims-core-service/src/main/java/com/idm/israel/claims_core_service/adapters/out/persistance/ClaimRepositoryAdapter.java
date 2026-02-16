package com.idm.israel.claims_core_service.adapters.out.persistance;

import com.idm.israel.claims_core_service.application.ports.out.ClaimRepositoryPort;
import com.idm.israel.claims_core_service.domain.model.Claim;
import com.idm.israel.claims_core_service.domain.model.ClaimStatus;
import com.idm.israel.claims_core_service.domain.model.ClaimType;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class ClaimRepositoryAdapter implements ClaimRepositoryPort {

    private final SpringDataClaimRepository repo;
    private final DatabaseClient db;

    public ClaimRepositoryAdapter(SpringDataClaimRepository repo, DatabaseClient db) {
        this.repo = repo;
        this.db = db;
    }

    @Override
    public Mono<Claim> save(Claim claim) {
        return repo.save(ClaimPersistenceMapper.toEntity(claim))
                .map(ClaimPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Claim> findById(UUID id) {
        return repo.findById(id).map(ClaimPersistenceMapper::toDomain);
    }

    @Override
    public Flux<Claim> search(ClaimStatus status, String policyNumber, String claimantId, ClaimType type) {
        StringBuilder sql = new StringBuilder(
                "SELECT id, policy_number, claimant_id, type, " +
                        "description, amount_requested, status, created_at, updated_at " +
                        "FROM claims WHERE 1=1"
        );

        if (status != null) sql.append(" AND status = :status");
        if (policyNumber != null && !policyNumber.isBlank()) sql.append(" AND policy_number = :policyNumber");
        if (claimantId != null && !claimantId.isBlank()) sql.append(" AND claimant_id = :claimantId");
        if (type != null) sql.append(" AND type = :type");

        DatabaseClient.GenericExecuteSpec spec = db.sql(sql.toString());
        if (status != null) spec = spec.bind("status", status.name());
        if (policyNumber != null && !policyNumber.isBlank()) spec = spec.bind("policyNumber", policyNumber);
        if (claimantId != null && !claimantId.isBlank()) spec = spec.bind("claimantId", claimantId);
        if (type != null) spec = spec.bind("type", type.name());

        return spec
                .map((row, meta) -> {
                    ClaimEntity e = new ClaimEntity();

                    String typeValue = row.get("type", String.class);
                    String statusValue = row.get("status", String.class);

                    e.setId(row.get("id", UUID.class));
                    e.setPolicyNumber(row.get("policy_number", String.class));
                    e.setClaimantId(row.get("claimant_id", String.class));

                    try {
                        if (typeValue != null) {
                            e.setType(ClaimType.valueOf(typeValue.trim().toUpperCase()));
                        }
                        if (statusValue != null) {
                            e.setStatus(ClaimStatus.valueOf(statusValue.trim().toUpperCase()));
                        }
                    } catch (IllegalArgumentException ex) {
                        throw new IllegalArgumentException(
                                "Enum parse error. type='" + typeValue + "' status='" + statusValue + "'",
                                ex
                        );
                    }

                    e.setDescription(row.get("description", String.class));
                    e.setAmountRequested(row.get("amount_requested", BigDecimal.class));
                    LocalDateTime cAt = row.get("created_at", LocalDateTime.class);
                    LocalDateTime uAt = row.get("updated_at", LocalDateTime.class);

                    e.setCreatedAt(cAt != null ? cAt.toInstant(ZoneOffset.UTC) : null);
                    e.setUpdatedAt(uAt != null ? uAt.toInstant(ZoneOffset.UTC) : null);

                    return ClaimPersistenceMapper.toDomain(e);
                })
                .all();
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return repo.deleteById(id);
    }

}