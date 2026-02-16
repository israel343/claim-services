package com.idm.israel.claims_orchestrator_service.adapters.out.persistence;

import com.idm.israel.claims_orchestrator_service.domain.model.FlowStatus;
import com.idm.israel.claims_orchestrator_service.domain.model.FlowStep;
import lombok.Data;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Table("flows")
public class FlowEntity {

    @Id
    @Column("id")
    private UUID id;

    @Column("claim_id")
    private UUID claimId;

    @Column("status")
    private FlowStatus status;

    @Column("last_step")
    private FlowStep lastStep;

    @Column("executed_steps")
    private String executedSteps; // CSV

    @Column("error_message")
    private String errorMessage;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Transient
    private boolean isNew;

    public FlowEntity markNew() { this.isNew = true; return this; }
    public FlowEntity markNotNew() { this.isNew = false; return this; }
}