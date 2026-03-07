package nl.wunderbieb.kms.api.rs.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record DocumentTopicMappingCreateRequest(
    @NotBlank String topicCode,
    @NotBlank String mappingSource
) {
}
