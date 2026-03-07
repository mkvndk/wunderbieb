package nl.wunderbieb.kms.api.rs.admin.dto;

import java.time.Instant;

public record DocumentTopicMappingResponse(
    long id,
    long documentId,
    long topicId,
    String topicCode,
    String mappingSource,
    Instant createdAt,
    long createdByUserId
) {
}
