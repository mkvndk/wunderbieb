package nl.wunderbieb.kms.api.rs.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record ScoreConfigurationCreateRequest(
    @NotBlank String code,
    int numericValue,
    @NotBlank String displayLabelNl,
    @NotBlank String descriptionNl,
    @PositiveOrZero int sortOrder
) {
}
