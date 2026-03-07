package nl.wunderbieb.kms.api.rs.docs.dto;

import java.time.Instant;
import nl.wunderbieb.kms.docs.domain.DocumentVersion;

public record DocumentVersionResponse(
    long id,
    int versionNumber,
    String status,
    String contentJson,
    String changeSummary,
    long createdByUserId,
    Instant createdAt
) {

  public static DocumentVersionResponse from(DocumentVersion version) {
    return new DocumentVersionResponse(
        version.id(),
        version.versionNumber(),
        version.status().name(),
        version.contentJson(),
        version.changeSummary(),
        version.createdByUserId(),
        version.createdAt()
    );
  }
}
