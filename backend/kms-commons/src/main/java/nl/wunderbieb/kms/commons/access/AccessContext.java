package nl.wunderbieb.kms.commons.access;

import java.util.Set;

public record AccessContext(
    boolean platformAdmin,
    ScopeType scopeType,
    PermissionLevel permissionLevel,
    Set<String> capabilityCodes
) {

  public AccessContext {
    capabilityCodes = capabilityCodes == null ? Set.of() : Set.copyOf(capabilityCodes);
  }

  public void requirePlatformAdmin() {
    if (!platformAdmin) {
      throw new IllegalStateException("Platform admin rechten zijn vereist.");
    }
  }

  public boolean hasCapability(String capabilityCode) {
    return platformAdmin || capabilityCodes.contains(capabilityCode);
  }
}
