package nl.wunderbieb.kms.users.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import nl.wunderbieb.kms.audit.service.AuditService;
import nl.wunderbieb.kms.commons.access.PermissionLevel;
import nl.wunderbieb.kms.commons.access.ScopeType;
import nl.wunderbieb.kms.users.domain.Capability;
import nl.wunderbieb.kms.users.domain.Role;

public final class RoleAdminService {

  private final AuditService auditService;
  private final AtomicLong roleSequence = new AtomicLong(0);
  private final AtomicLong capabilitySequence = new AtomicLong(0);
  private final Map<Long, Role> rolesById = new LinkedHashMap<>();
  private final Map<String, Role> rolesByCode = new LinkedHashMap<>();
  private final Map<Long, Capability> capabilitiesById = new LinkedHashMap<>();
  private final Map<String, Capability> capabilitiesByCode = new LinkedHashMap<>();

  public RoleAdminService(AuditService auditService) {
    this.auditService = auditService;
    seedDefaults();
  }

  public synchronized List<Role> getRoles() {
    return List.copyOf(rolesById.values());
  }

  public synchronized List<Capability> getCapabilities() {
    return List.copyOf(capabilitiesById.values());
  }

  public synchronized Role createRole(
      String actorRoleCode,
      String code,
      String displayNameNl,
      String descriptionNl,
      ScopeType scopeType,
      PermissionLevel permissionLevel,
      Set<String> capabilityCodes
  ) {
    if (rolesByCode.containsKey(code)) {
      throw new IllegalArgumentException("Rolcode bestaat al.");
    }
    requireKnownCapabilities(capabilityCodes);
    Role role = new Role(
        roleSequence.incrementAndGet(),
        code,
        displayNameNl,
        descriptionNl,
        scopeType,
        permissionLevel,
        false,
        true,
        Set.copyOf(capabilityCodes)
    );
    rolesById.put(role.id(), role);
    rolesByCode.put(role.code(), role);
    auditService.record("role_created", actorRoleCode, "role", role.code(), "Rol aangemaakt");
    return role;
  }

  public synchronized Role updateRole(
      String actorRoleCode,
      long roleId,
      String displayNameNl,
      String descriptionNl,
      Boolean active,
      Set<String> capabilityCodes
  ) {
    Role existing = requireRole(roleId);
    if (capabilityCodes != null) {
      requireKnownCapabilities(capabilityCodes);
    }
    Role updated = existing.withPatch(displayNameNl, descriptionNl, active, capabilityCodes);
    rolesById.put(roleId, updated);
    rolesByCode.put(updated.code(), updated);
    auditService.record("role_updated", actorRoleCode, "role", updated.code(), "Rol bijgewerkt");
    return updated;
  }

  public synchronized Capability createCapability(String actorRoleCode, String code, String displayNameNl, String descriptionNl) {
    if (capabilitiesByCode.containsKey(code)) {
      throw new IllegalArgumentException("Capabilitycode bestaat al.");
    }
    Capability capability = new Capability(
        capabilitySequence.incrementAndGet(),
        code,
        displayNameNl,
        descriptionNl,
        true
    );
    capabilitiesById.put(capability.id(), capability);
    capabilitiesByCode.put(capability.code(), capability);
    auditService.record("capability_created", actorRoleCode, "capability", capability.code(), "Capability aangemaakt");
    return capability;
  }

  public synchronized Capability updateCapability(
      String actorRoleCode,
      long capabilityId,
      String displayNameNl,
      String descriptionNl,
      Boolean active
  ) {
    Capability existing = requireCapability(capabilityId);
    Capability updated = existing.withPatch(displayNameNl, descriptionNl, active);
    capabilitiesById.put(capabilityId, updated);
    capabilitiesByCode.put(updated.code(), updated);
    auditService.record("capability_updated", actorRoleCode, "capability", updated.code(), "Capability bijgewerkt");
    return updated;
  }

  public synchronized Role requireRoleByCode(String roleCode) {
    Role role = rolesByCode.get(roleCode);
    if (role == null) {
      throw new NoSuchElementException("Onbekende rol.");
    }
    return role;
  }

  private Role requireRole(long roleId) {
    Role role = rolesById.get(roleId);
    if (role == null) {
      throw new NoSuchElementException("Rol niet gevonden.");
    }
    return role;
  }

  private Capability requireCapability(long capabilityId) {
    Capability capability = capabilitiesById.get(capabilityId);
    if (capability == null) {
      throw new NoSuchElementException("Capability niet gevonden.");
    }
    return capability;
  }

  private void requireKnownCapabilities(Set<String> capabilityCodes) {
    List<String> unknownCodes = new ArrayList<>();
    for (String capabilityCode : capabilityCodes) {
      if (!capabilitiesByCode.containsKey(capabilityCode)) {
        unknownCodes.add(capabilityCode);
      }
    }
    if (!unknownCodes.isEmpty()) {
      throw new IllegalArgumentException("Onbekende capabilities: " + String.join(", ", unknownCodes));
    }
  }

  private void seedDefaults() {
    createSeedCapability("MANAGE_USERS", "Gebruikers beheren", "Mag gebruikers en rollen beheren");
    createSeedCapability("MANAGE_ORG", "Organisatie beheren", "Mag besturen en scholen beheren");
    createSeedCapability("MANAGE_TAXONOMY", "Taxonomie beheren", "Mag domeinen, onderwerpen en documenttypen beheren");
    createSeedCapability("APPROVE_DOCUMENT", "Document goedkeuren", "Mag documentstatus goedkeuren");
    createSeedCapability("EXPORT_DATA", "Data exporteren", "Mag exports starten");
    createSeedCapability("MANAGE_SCORE_CONFIGURATION", "Scoreconfiguratie beheren", "Mag scores en regels aanpassen");

    createSeedRole("PLATFORM_ADMIN", "Super admin", "Platformbeheerder met volledige beheerrechten",
        ScopeType.PLATFORM, PermissionLevel.WRITE,
        Set.of("MANAGE_USERS", "MANAGE_ORG", "MANAGE_TAXONOMY", "MANAGE_SCORE_CONFIGURATION", "EXPORT_DATA"));
    createSeedRole("BESTUURDER", "Bestuurder", "Lezen op bestuursniveau",
        ScopeType.BOARD, PermissionLevel.READ, Set.of());
    createSeedRole("BESTUURS_KC", "Bestuurs kwaliteitscoordinator", "Schrijven op bestuursniveau",
        ScopeType.BOARD, PermissionLevel.WRITE, Set.of("APPROVE_DOCUMENT"));
    createSeedRole("DIRECTEUR", "Directeur", "Schrijven op schoolniveau",
        ScopeType.SCHOOL, PermissionLevel.WRITE, Set.of("MANAGE_USERS", "APPROVE_DOCUMENT"));
    createSeedRole("KWALITEITSCOORDINATOR", "Kwaliteitscoordinator", "Schrijven op schoolniveau",
        ScopeType.SCHOOL, PermissionLevel.WRITE, Set.of());
    createSeedRole("TEAMLID", "Teamlid", "Lezen op schoolniveau",
        ScopeType.SCHOOL, PermissionLevel.READ, Set.of());
    createSeedRole("MR_LID", "MR-lid", "Lezen op schoolniveau",
        ScopeType.SCHOOL, PermissionLevel.READ, Set.of());
    createSeedRole("ONDERWIJSADVISEUR", "Onderwijsadviseur", "Externe adviseur met eigen scope",
        ScopeType.EXTERN, PermissionLevel.WRITE, Set.of());
  }

  private void createSeedCapability(String code, String displayNameNl, String descriptionNl) {
    Capability capability = new Capability(
        capabilitySequence.incrementAndGet(),
        code,
        displayNameNl,
        descriptionNl,
        true
    );
    capabilitiesById.put(capability.id(), capability);
    capabilitiesByCode.put(capability.code(), capability);
  }

  private void createSeedRole(
      String code,
      String displayNameNl,
      String descriptionNl,
      ScopeType scopeType,
      PermissionLevel permissionLevel,
      Set<String> capabilityCodes
  ) {
    Role role = new Role(
        roleSequence.incrementAndGet(),
        code,
        displayNameNl,
        descriptionNl,
        scopeType,
        permissionLevel,
        true,
        true,
        Set.copyOf(capabilityCodes)
    );
    rolesById.put(role.id(), role);
    rolesByCode.put(role.code(), role);
  }
}
