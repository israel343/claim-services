package com.idm.israel.claims_orchestrator_service.adapters.in;

import com.idm.israel.claims_orchestrator_service.application.dto.FlowResponse;
import com.idm.israel.claims_orchestrator_service.application.dto.FlowStatusResponse;
import com.idm.israel.claims_orchestrator_service.application.dto.RetryFlowResponse;
import com.idm.israel.claims_orchestrator_service.application.dto.StartFlowCommand;
import com.idm.israel.claims_orchestrator_service.application.ports.in.GetFlowStatusUseCase;
import com.idm.israel.claims_orchestrator_service.application.ports.in.RetryFlowUseCase;
import com.idm.israel.claims_orchestrator_service.application.ports.in.StartClaimFlowUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;
@Tag(name = "Flows")
@RestController
@RequestMapping("/api/v1/flows")
public class FlowController {

    private final StartClaimFlowUseCase startUC;
    private final GetFlowStatusUseCase statusUC;
    private final RetryFlowUseCase retryUC;

    public FlowController(StartClaimFlowUseCase startUC, GetFlowStatusUseCase statusUC, RetryFlowUseCase retryUC) {
        this.startUC = startUC;
        this.statusUC = statusUC;
        this.retryUC = retryUC;
    }

    @Operation(summary = "Start a full claim flow")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Flow result"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "500", description = "Unexpected error")
    })
    @PostMapping("/claims")
    public Mono<FlowResponse> startClaimFlow(@RequestBody StartFlowCommand cmd) {
        return startUC.start(cmd);
    }

    @Operation(summary = "Get flow status")
    @GetMapping("/{flowId}")
    public Mono<FlowStatusResponse> getStatus(@PathVariable UUID flowId) {
        return statusUC.get(flowId);
    }

    @Operation(summary = "Retry a flow (FAILED/COMPENSATED only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retry result"),
            @ApiResponse(responseCode = "409", description = "Retry not allowed")
    })
    @PostMapping("/{flowId}/retry")
    public Mono<RetryFlowResponse> retry(@PathVariable UUID flowId) {
        return retryUC.retry(flowId);
    }
}