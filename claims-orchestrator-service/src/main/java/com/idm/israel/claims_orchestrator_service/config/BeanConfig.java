package com.idm.israel.claims_orchestrator_service.config;

import com.idm.israel.claims_orchestrator_service.adapters.out.http.WebClientClaimsCoreAdapter;
import com.idm.israel.claims_orchestrator_service.adapters.out.persistence.FlowRepositoryAdapter;
import com.idm.israel.claims_orchestrator_service.adapters.out.persistence.SpringDataFlowRepository;
import com.idm.israel.claims_orchestrator_service.application.policy.ApprovalPolicy;
import com.idm.israel.claims_orchestrator_service.application.policy.DefaultApprovalPolicy;
import com.idm.israel.claims_orchestrator_service.application.ports.in.GetFlowStatusUseCase;
import com.idm.israel.claims_orchestrator_service.application.ports.in.RetryFlowUseCase;
import com.idm.israel.claims_orchestrator_service.application.ports.in.StartClaimFlowUseCase;
import com.idm.israel.claims_orchestrator_service.application.ports.out.ClaimsCorePort;
import com.idm.israel.claims_orchestrator_service.application.ports.out.FlowRepositoryPort;
import com.idm.israel.claims_orchestrator_service.application.service.FlowEngine;
import com.idm.israel.claims_orchestrator_service.application.service.GetFlowStatusService;
import com.idm.israel.claims_orchestrator_service.application.service.RetryFlowService;
import com.idm.israel.claims_orchestrator_service.application.service.StartClaimFlowService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;

@Configuration
public class BeanConfig {

    @Bean
    public ClaimsCorePort claimsCorePort(WebClient coreWebClient) {
        return new WebClientClaimsCoreAdapter(coreWebClient);
    }

    @Bean
    public FlowRepositoryPort flowRepositoryPort(SpringDataFlowRepository repo) {
        return new FlowRepositoryAdapter(repo);
    }

    @Bean
    public ApprovalPolicy approvalPolicy(@Value("${flow.auto-approve-threshold:1000}") BigDecimal threshold) {
        return new DefaultApprovalPolicy(threshold);
    }

    @Bean
    public FlowEngine flowEngine(ClaimsCorePort core, FlowRepositoryPort flowRepo, ApprovalPolicy policy) {
        return new FlowEngine(core, flowRepo, policy);
    }

    @Bean
    public StartClaimFlowUseCase startClaimFlowUseCase(FlowEngine engine) {
        return new StartClaimFlowService(engine);
    }

    @Bean
    public GetFlowStatusUseCase getFlowStatusUseCase(FlowRepositoryPort repo) {
        return new GetFlowStatusService(repo);
    }

    @Bean
    public RetryFlowUseCase retryFlowUseCase(FlowRepositoryPort repo, FlowEngine engine) {
        return new RetryFlowService(repo, engine);
    }
}
