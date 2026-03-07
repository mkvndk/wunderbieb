package nl.wunderbieb.kms.api.rs.session.dto;

import java.util.Set;
import nl.wunderbieb.kms.api.security.AccessContext;

public record CurrentSessionResponse(
    Long userId,
    String preferredUsername,
    String fullName,
    String email,
    String roleCode,
    String scopeType,
    String permissionLevel,
    Set<String> capabilities,
    Long boardId,
    Long schoolId
) {

  public static CurrentSessionResponse from(
      AccessContext accessContext,
      Long userId,
      String preferredUsername,
      String fullName,
      String email,
      Long boardId,
      Long schoolId
  ) {
    return new CurrentSessionResponse(
        userId,
        preferredUsername,
        fullName,
        email,
        accessContext.roleCode(),
        accessContext.scopeType().name(),
        accessContext.permissionLevel().name(),
        accessContext.capabilities(),
        boardId,
        schoolId
    );
  }
}
