package nl.wunderbieb.kms.api.rs.docs.dto;

import java.time.Instant;
import java.util.List;
import nl.wunderbieb.kms.docs.domain.DocumentSnapshot;

public record DocumentResponse(
    long id,
    String documentTypeCode,
    String title,
    String summary,
    String sourceReference,
    Instant publishedAt,
    String scopeType,
    Long boardId,
    Long schoolId,
    long createdByUserId,
    int activeVersionNumber,
    String workflowStatus,
    boolean active,
    Instant createdAt,
    Instant updatedAt,
    String onboardingStatus,
    Instant lastReadAt,
    Instant onboardingUpdatedAt,
    List<DocumentVersionResponse> versions
) {

  public static DocumentResponse from(DocumentSnapshot snapshot) {
    return new DocumentResponse(
        snapshot.id(),
        snapshot.documentTypeCode(),
        snapshot.title(),
        snapshot.summary(),
        snapshot.sourceReference(),
        snapshot.publishedAt(),
        snapshot.scopeType().name(),
        snapshot.boardId(),
        snapshot.schoolId(),
        snapshot.createdByUserId(),
        snapshot.activeVersionNumber(),
        snapshot.workflowStatus().name(),
        snapshot.active(),
        snapshot.createdAt(),
        snapshot.updatedAt(),
        snapshot.onboardingStatus().name(),
        snapshot.lastReadAt(),
        snapshot.onboardingUpdatedAt(),
        snapshot.versions().stream().map(DocumentVersionResponse::from).toList()
    );
  }
}
