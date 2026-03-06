package nl.wunderbieb.kms.api.rs.admin.dto;

import java.time.Instant;

public record AssignmentPatchRequest(
    String roleCode,
    Instant validFrom,
    Instant validTo,
    Boolean active
) {
}
