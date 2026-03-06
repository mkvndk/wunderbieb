package nl.wunderbieb.kms.api.rs.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record DocumentTypeCreateRequest(
    @NotBlank String code,
    @NotBlank String displayNameNl,
    @NotBlank String descriptionNl,
    boolean requiredForOnboarding
) {
}
