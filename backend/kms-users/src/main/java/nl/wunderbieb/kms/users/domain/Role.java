package nl.wunderbieb.kms.users.domain;

import java.util.Set;
import nl.wunderbieb.kms.commons.access.PermissionLevel;
import nl.wunderbieb.kms.commons.access.ScopeType;

public record Role(
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

  public Role withPatch(String displayNameNl, String descriptionNl, Boolean active, Set<String> capabilityCodes) {
    return new Role(
        id,
        code,
        displayNameNl != null ? displayNameNl : this.displayNameNl,
        descriptionNl != null ? descriptionNl : this.descriptionNl,
        scopeType,
        permissionLevel,
        systemRole,
        active != null ? active : this.active,
        capabilityCodes != null ? Set.copyOf(capabilityCodes) : this.capabilityCodes
    );
  }
}
