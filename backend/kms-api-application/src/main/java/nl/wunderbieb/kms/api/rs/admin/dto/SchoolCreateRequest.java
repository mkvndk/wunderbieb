package nl.wunderbieb.kms.api.rs.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record SchoolCreateRequest(
    @NotBlank String brin,
    @NotBlank String displayNameNl
) {
}
