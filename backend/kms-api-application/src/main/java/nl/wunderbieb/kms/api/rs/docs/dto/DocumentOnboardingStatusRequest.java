package nl.wunderbieb.kms.api.rs.docs.dto;

import jakarta.validation.constraints.NotNull;
import nl.wunderbieb.kms.docs.domain.DocumentOnboardingStatus;

public record DocumentOnboardingStatusRequest(
    @NotNull DocumentOnboardingStatus status
) {
}
