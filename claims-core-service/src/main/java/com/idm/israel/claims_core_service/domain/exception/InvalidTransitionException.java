package com.idm.israel.claims_core_service.domain.exception;

import com.idm.israel.claims_core_service.domain.model.ClaimStatus;

public class InvalidTransitionException extends DomainException {
  public InvalidTransitionException(ClaimStatus from, ClaimStatus to) {
    super("Invalid status transition: " + from + " -> " + to);
  }
}