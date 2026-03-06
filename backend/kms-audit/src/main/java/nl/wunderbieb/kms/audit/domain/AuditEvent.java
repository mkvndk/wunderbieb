package nl.wunderbieb.kms.audit.domain;

import java.time.Instant;

public record AuditEvent(
    long id,
    String action,
    String actorRoleCode,
    String entityType,
    String entityId,
    String summary,
    Instant occurredAt
) {
}
