package nl.wunderbieb.kms.docs.domain;

import java.time.Instant;
import java.util.List;
import nl.wunderbieb.kms.commons.access.ScopeType;

public record DocumentSnapshot(
    long id,
    String documentTypeCode,
    String title,
    String summary,
    String sourceReference,
    Instant publishedAt,
    ScopeType scopeType,
    Long boardId,
    Long schoolId,
    long createdByUserId,
    int activeVersionNumber,
    DocumentWorkflowStatus workflowStatus,
    boolean active,
    Instant createdAt,
    Instant updatedAt,
    DocumentOnboardingStatus onboardingStatus,
    Instant lastReadAt,
    Instant onboardingUpdatedAt,
    List<DocumentVersion> versions
) {
}
