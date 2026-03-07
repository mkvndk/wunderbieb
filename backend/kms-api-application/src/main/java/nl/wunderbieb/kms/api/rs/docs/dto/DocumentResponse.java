package nl.wunderbieb.kms.api.rs.docs.dto;

import java.time.Instant;
import java.util.List;
import nl.wunderbieb.kms.docs.domain.DocumentSnapshot;

public record DocumentResponse(
    long id,
    String documentTypeCode,
    String title,
    String scopeType,
    Long boardId,
    Long schoolId,
    long createdByUserId,
    int activeVersionNumber,
    String workflowStatus,
    boolean active,
    Instant createdAt,
    Instant updatedAt,
    List<DocumentVersionResponse> versions
) {

  public static DocumentResponse from(DocumentSnapshot snapshot) {
    return new DocumentResponse(
        snapshot.id(),
        snapshot.documentTypeCode(),
        snapshot.title(),
        snapshot.scopeType().name(),
        snapshot.boardId(),
        snapshot.schoolId(),
        snapshot.createdByUserId(),
        snapshot.activeVersionNumber(),
        snapshot.workflowStatus().name(),
        snapshot.active(),
        snapshot.createdAt(),
        snapshot.updatedAt(),
        snapshot.versions().stream().map(DocumentVersionResponse::from).toList()
    );
  }
}
