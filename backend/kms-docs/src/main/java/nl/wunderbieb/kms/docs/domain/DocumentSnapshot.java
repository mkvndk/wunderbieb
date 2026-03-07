package nl.wunderbieb.kms.docs.domain;

import java.time.Instant;
import java.util.List;
import nl.wunderbieb.kms.commons.access.ScopeType;

public record DocumentSnapshot(
    long id,
    String documentTypeCode,
    String title,
    ScopeType scopeType,
    Long boardId,
    Long schoolId,
    long createdByUserId,
    int activeVersionNumber,
    DocumentWorkflowStatus workflowStatus,
    boolean active,
    Instant createdAt,
    Instant updatedAt,
    List<DocumentVersion> versions
) {
}
