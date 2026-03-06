package nl.wunderbieb.kms.users.model;

import java.time.Instant;
import nl.wunderbieb.kms.commons.access.PermissionLevel;
import nl.wunderbieb.kms.commons.access.ScopeType;

public record UserAssignmentDefinition(
    long id,
    String userId,
    String roleCode,
    ScopeType scopeType,
    Long boardId,
    Long schoolId,
    PermissionLevel permissionLevel,
    boolean active,
    Instant validFrom,
    Instant validTo
) {
}
