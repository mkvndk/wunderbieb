package nl.wunderbieb.kms.users.model;

import java.util.Set;
import nl.wunderbieb.kms.commons.access.PermissionLevel;
import nl.wunderbieb.kms.commons.access.ScopeType;

public record RoleDefinition(
    long id,
    String code,
    String displayNameNl,
    String descriptionNl,
    ScopeType scopeType,
    PermissionLevel permissionLevel,
    boolean systemRole,
    boolean active,
    Set<String> capabilityCodes
) {

  public RoleDefinition {
    capabilityCodes = capabilityCodes == null ? Set.of() : Set.copyOf(capabilityCodes);
  }
}
