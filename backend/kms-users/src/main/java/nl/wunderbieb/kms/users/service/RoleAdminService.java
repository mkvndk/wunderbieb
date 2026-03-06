package nl.wunderbieb.kms.users.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import nl.wunderbieb.kms.audit.service.AuditService;
import nl.wunderbieb.kms.commons.access.PermissionLevel;
import nl.wunderbieb.kms.commons.access.ScopeType;
import nl.wunderbieb.kms.users.domain.Capability;
import nl.wunderbieb.kms.users.domain.Role;
import nl.wunderbieb.kms.users.repository.CapabilityEntity;
import nl.wunderbieb.kms.users.repository.CapabilityRepository;
import nl.wunderbieb.kms.users.repository.RoleEntity;
import nl.wunderbieb.kms.users.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RoleAdminService {

  private final AuditService auditService;
  private final RoleRepository roleRepository;
  private final CapabilityRepository capabilityRepository;

  public RoleAdminService(AuditService auditService, RoleRepository roleRepository, CapabilityRepository capabilityRepository) {
    this.auditService = auditService;
    this.roleRepository = roleRepository;
    this.capabilityRepository = capabilityRepository;
  }

  @Transactional(readOnly = true)
  public List<Role> getRoles() {
    return roleRepository.findAllByOrderByIdAsc().stream()
        .map(this::toDomain)
        .toList();
  }

  @Transactional(readOnly = true)
  public List<Capability> getCapabilities() {
    return capabilityRepository.findAllByOrderByIdAsc().stream()
        .map(this::toDomain)
        .toList();
  }

  public Role createRole(
      String actorRoleCode,
      String code,
      String displayNameNl,
      String descriptionNl,
      ScopeType scopeType,
      PermissionLevel permissionLevel,
      Set<String> capabilityCodes
  ) {
    if (roleRepository.existsByCode(code)) {
      throw new IllegalArgumentException("Rolcode bestaat al.");
    }
    requireKnownCapabilities(capabilityCodes);
    RoleEntity role = roleRepository.save(new RoleEntity(
        code,
        displayNameNl,
        descriptionNl,
        scopeType,
        permissionLevel,
        false,
        true,
        Set.copyOf(capabilityCodes)
    ));
    auditService.record("role_created", actorRoleCode, "role", role.getCode(), "Rol aangemaakt");
    return toDomain(role);
  }

  public Role updateRole(
      String actorRoleCode,
      long roleId,
      String displayNameNl,
      String descriptionNl,
      Boolean active,
      Set<String> capabilityCodes
  ) {
    RoleEntity existing = requireRole(roleId);
    if (capabilityCodes != null) {
      requireKnownCapabilities(capabilityCodes);
      existing.getCapabilityCodes().clear();
      existing.getCapabilityCodes().addAll(capabilityCodes);
    }
    if (displayNameNl != null) {
      existing.setDisplayNameNl(displayNameNl);
    }
    if (descriptionNl != null) {
      existing.setDescriptionNl(descriptionNl);
    }
    if (active != null) {
      existing.setActive(active);
    }
    RoleEntity updated = roleRepository.save(existing);
    auditService.record("role_updated", actorRoleCode, "role", updated.getCode(), "Rol bijgewerkt");
    return toDomain(updated);
  }

  public Capability createCapability(String actorRoleCode, String code, String displayNameNl, String descriptionNl) {
    if (capabilityRepository.existsByCode(code)) {
      throw new IllegalArgumentException("Capabilitycode bestaat al.");
    }
    CapabilityEntity capability = capabilityRepository.save(new CapabilityEntity(
        code,
        displayNameNl,
        descriptionNl,
        true
    ));
    auditService.record("capability_created", actorRoleCode, "capability", capability.getCode(), "Capability aangemaakt");
    return toDomain(capability);
  }

  public Capability updateCapability(
      String actorRoleCode,
      long capabilityId,
      String displayNameNl,
      String descriptionNl,
      Boolean active
  ) {
    CapabilityEntity existing = requireCapability(capabilityId);
    if (displayNameNl != null) {
      existing.setDisplayNameNl(displayNameNl);
    }
    if (descriptionNl != null) {
      existing.setDescriptionNl(descriptionNl);
    }
    if (active != null) {
      existing.setActive(active);
    }
    CapabilityEntity updated = capabilityRepository.save(existing);
    auditService.record("capability_updated", actorRoleCode, "capability", updated.getCode(), "Capability bijgewerkt");
    return toDomain(updated);
  }

  @Transactional(readOnly = true)
  public Role requireRoleByCode(String roleCode) {
    return toDomain(roleRepository.findByCode(roleCode)
        .orElseThrow(() -> new NoSuchElementException("Onbekende rol.")));
  }

  @Transactional(readOnly = true)
  public Capability requireCapabilityByCode(String capabilityCode) {
    return toDomain(capabilityRepository.findByCode(capabilityCode)
        .orElseThrow(() -> new NoSuchElementException("Onbekende capability.")));
  }

  private RoleEntity requireRole(long roleId) {
    return roleRepository.findById(roleId)
        .orElseThrow(() -> new NoSuchElementException("Rol niet gevonden."));
  }

  private CapabilityEntity requireCapability(long capabilityId) {
    return capabilityRepository.findById(capabilityId)
        .orElseThrow(() -> new NoSuchElementException("Capability niet gevonden."));
  }

  private void requireKnownCapabilities(Set<String> capabilityCodes) {
    List<String> unknownCodes = capabilityCodes.stream()
        .filter(capabilityCode -> !capabilityRepository.existsByCode(capabilityCode))
        .sorted()
        .toList();
    if (!unknownCodes.isEmpty()) {
      throw new IllegalArgumentException("Onbekende capabilities: " + String.join(", ", unknownCodes));
    }
  }

  private Role toDomain(RoleEntity role) {
    return new Role(
        role.getId(),
        role.getCode(),
        role.getDisplayNameNl(),
        role.getDescriptionNl(),
        role.getScopeType(),
        role.getPermissionLevel(),
        role.isSystemRole(),
        role.isActive(),
        Set.copyOf(role.getCapabilityCodes())
    );
  }

  private Capability toDomain(CapabilityEntity capability) {
    return new Capability(
        capability.getId(),
        capability.getCode(),
        capability.getDisplayNameNl(),
        capability.getDescriptionNl(),
        capability.isActive()
    );
  }
}
