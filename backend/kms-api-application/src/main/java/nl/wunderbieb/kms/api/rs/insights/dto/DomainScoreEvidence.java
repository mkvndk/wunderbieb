package nl.wunderbieb.kms.api.rs.insights.dto;

import java.time.Instant;

public record DomainScoreEvidence(
    long documentId,
    String title,
    String workflowStatus,
    Instant updatedAt
) {
}
