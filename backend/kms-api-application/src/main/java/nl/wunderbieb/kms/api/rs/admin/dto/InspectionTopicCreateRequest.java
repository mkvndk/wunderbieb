package nl.wunderbieb.kms.api.rs.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record InspectionTopicCreateRequest(
    @NotBlank String domainCode,
    @NotBlank String code,
    @NotBlank String displayNameNl,
    @NotBlank String descriptionNl,
    @PositiveOrZero int sortOrder
) {
}
