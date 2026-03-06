package nl.wunderbieb.kms.api.rs.admin.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import nl.wunderbieb.kms.commons.access.ScopeType;

public record AssignmentCreateRequest(
    @NotBlank String roleCode,
    ScopeType scopeType,
    Long boardId,
    Long schoolId,
    Instant validFrom,
    Instant validTo
) {
}
