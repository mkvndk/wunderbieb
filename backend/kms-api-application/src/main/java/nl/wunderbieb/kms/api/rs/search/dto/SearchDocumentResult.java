package nl.wunderbieb.kms.api.rs.search.dto;

import java.time.Instant;

public record SearchDocumentResult(
    long id,
    String title,
    String summary,
    String documentTypeCode,
    String workflowStatus,
    Long schoolId,
    String onboardingStatus,
    Instant lastReadAt,
    Instant updatedAt
) {
}
