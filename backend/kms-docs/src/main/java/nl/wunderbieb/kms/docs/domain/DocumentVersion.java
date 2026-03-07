package nl.wunderbieb.kms.docs.domain;

import java.time.Instant;

public record DocumentVersion(
    long id,
    int versionNumber,
    DocumentVersionStatus status,
    String contentJson,
    String changeSummary,
    long createdByUserId,
    Instant createdAt
) {
}
