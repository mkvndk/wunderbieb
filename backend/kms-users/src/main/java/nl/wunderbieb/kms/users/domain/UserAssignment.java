package nl.wunderbieb.kms.users.domain;

import java.time.Instant;
import nl.wunderbieb.kms.commons.access.PermissionLevel;
import nl.wunderbieb.kms.commons.access.ScopeType;

public record UserAssignment(
    long id,
    long userId,
    String roleCode,
    ScopeType scopeType,
    Long boardId,
    Long schoolId,
    PermissionLevel permissionLevel,
    boolean active,
    Instant validFrom,
    Instant validTo
) {

  public UserAssignment withPatch(String roleCode, Instant validFrom, Instant validTo, Boolean active) {
    return new UserAssignment(
        id,
        userId,
        roleCode != null ? roleCode : this.roleCode,
        scopeType,
        boardId,
        schoolId,
        permissionLevel,
        active != null ? active : this.active,
        validFrom != null ? validFrom : this.validFrom,
        validTo != null ? validTo : this.validTo
    );
  }
}
