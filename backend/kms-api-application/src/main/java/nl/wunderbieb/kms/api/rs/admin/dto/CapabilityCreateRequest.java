package nl.wunderbieb.kms.api.rs.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record CapabilityCreateRequest(
    @NotBlank String code,
    @NotBlank String displayNameNl,
    @NotBlank String descriptionNl
) {
}
