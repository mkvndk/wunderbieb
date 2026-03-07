package nl.wunderbieb.kms.api.rs.docs.dto;

import jakarta.validation.constraints.NotBlank;

public record DocumentCreateRequest(
    @NotBlank String documentTypeCode,
    @NotBlank String title,
    @NotBlank String contentJson
) {
}
