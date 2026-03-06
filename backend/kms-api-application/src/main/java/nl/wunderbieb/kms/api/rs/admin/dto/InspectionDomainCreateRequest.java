package nl.wunderbieb.kms.api.rs.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record InspectionDomainCreateRequest(
    @NotBlank String code,
    @NotBlank String displayNameNl,
    @NotBlank String descriptionNl,
    @PositiveOrZero int sortOrder
) {
}
