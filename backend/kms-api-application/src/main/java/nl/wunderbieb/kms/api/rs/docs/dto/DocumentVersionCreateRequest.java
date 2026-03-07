package nl.wunderbieb.kms.api.rs.docs.dto;

import jakarta.validation.constraints.NotBlank;

public record DocumentVersionCreateRequest(
    @NotBlank String contentJson,
    String changeSummary
) {
}
