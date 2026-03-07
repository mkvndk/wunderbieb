package nl.wunderbieb.kms.api.rs.docs.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

public record DocumentCreateRequest(
    @NotBlank String documentTypeCode,
    @NotBlank String title,
    String summary,
    String sourceReference,
    Instant publishedAt,
    @NotBlank String contentJson
) {
}
