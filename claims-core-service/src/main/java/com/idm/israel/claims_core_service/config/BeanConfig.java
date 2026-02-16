package com.idm.israel.claims_core_service.config;

import com.idm.israel.claims_core_service.adapters.out.persistance.ClaimRepositoryAdapter;
import com.idm.israel.claims_core_service.adapters.out.persistance.SpringDataClaimRepository;
import com.idm.israel.claims_core_service.application.ports.out.ClaimRepositoryPort;
import com.idm.israel.claims_core_service.application.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;

@Configuration
public class BeanConfig {

    @Bean
    public ClaimRepositoryPort claimRepositoryPort(SpringDataClaimRepository repo, DatabaseClient db) {
        return new ClaimRepositoryAdapter(repo, db);
    }

    @Bean
    public CreateClaimService createClaimService(ClaimRepositoryPort repo) {
        return new CreateClaimService(repo);
    }

    @Bean
    public GetClaimService getClaimService(ClaimRepositoryPort repo) {
        return new GetClaimService(repo);
    }

    @Bean
    public SearchClaimsService searchClaimsService(ClaimRepositoryPort repo) {
        return new SearchClaimsService(repo);
    }

    @Bean
    public UpdateClaimService updateClaimService(ClaimRepositoryPort repo) {
        return new UpdateClaimService(repo);
    }

    @Bean
    public ClaimActionsService claimActionsService(ClaimRepositoryPort repo) {
        return new ClaimActionsService(repo);
    }
}