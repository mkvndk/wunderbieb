package nl.wunderbieb.kms.api.rs.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record BoardSchoolRelationCreateRequest(
    @Positive long boardId,
    @Positive long schoolId,
    @NotBlank String relationType
) {
}
