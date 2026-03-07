package nl.wunderbieb.kms.api.rs.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record BoardCreateRequest(
    @NotBlank String code,
    @NotBlank String displayNameNl
) {
}
