package nl.wunderbieb.kms.api.security;

import java.util.Set;
import nl.wunderbieb.kms.commons.access.PermissionLevel;
import nl.wunderbieb.kms.commons.access.ScopeType;

public record AccessContext(
    String roleCode,
    ScopeType scopeType,
    PermissionLevel permissionLevel,
    Set<String> capabilities
) {

  public void requireCapability(String capabilityCode) {
    if (!capabilities.contains(capabilityCode)) {
      throw new AccessDeniedException(capabilityCode);
    }
  }
}
