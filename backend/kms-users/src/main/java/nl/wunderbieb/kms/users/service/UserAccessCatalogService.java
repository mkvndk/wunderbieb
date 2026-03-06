package nl.wunderbieb.kms.users.service;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import nl.wunderbieb.kms.users.model.CapabilityDefinition;
import nl.wunderbieb.kms.users.model.RoleDefinition;
import nl.wunderbieb.kms.users.model.UserAssignmentDefinition;
import nl.wunderbieb.kms.users.repository.CapabilityEntity;
import nl.wunderbieb.kms.users.repository.CapabilityRepository;
import nl.wunderbieb.kms.users.repository.RoleEntity;
import nl.wunderbieb.kms.users.repository.RoleRepository;
import nl.wunderbieb.kms.users.repository.UserAssignmentEntity;
import nl.wunderbieb.kms.users.repository.UserAssignmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserAccessCatalogService {

  private final CapabilityRepository capabilityRepository;
  private final RoleRepository roleRepository;
  private final UserAssignmentRepository userAssignmentRepository;

  public UserAccessCatalogService(
      CapabilityRepository capabilityRepository,
      RoleRepository roleRepository,
      UserAssignmentRepository userAssignmentRepository
  ) {
    this.capabilityRepository = capabilityRepository;
    this.roleRepository = roleRepository;
    this.userAssignmentRepository = userAssignmentRepository;
  }

  public List<RoleDefinition> findRoles(String scopeType, Boolean active) {
    return roleRepository.findAllByOrderByIdAsc().stream()
        .map(this::toRoleDefinition)
        .filter(role -> scopeType == null || role.scopeType().name().equalsIgnoreCase(scopeType))
        .filter(role -> active == null || role.active() == active)
        .toList();
  }

  public List<CapabilityDefinition> findCapabilities() {
    return capabilityRepository.findAllByOrderByIdAsc().stream()
        .map(this::toCapabilityDefinition)
        .toList();
  }

  public List<UserAssignmentDefinition> findAssignments(String userId) {
    String normalizedUserId = userId.toLowerCase(Locale.ROOT);
    return userAssignmentRepository.findAll().stream()
        .map(this::toAssignmentDefinition)
        .filter(assignment -> String.valueOf(assignment.userId()).toLowerCase(Locale.ROOT).equals(normalizedUserId))
        .toList();
  }

  private RoleDefinition toRoleDefinition(RoleEntity entity) {
    return new RoleDefinition(
        entity.getId(),
        entity.getCode(),
        entity.getDisplayNameNl(),
        entity.getDescriptionNl(),
        entity.getScopeType(),
        entity.getPermissionLevel(),
        entity.isSystemRole(),
        entity.isActive(),
        entity.getCapabilityCodes()
    );
  }

  private CapabilityDefinition toCapabilityDefinition(CapabilityEntity entity) {
    return new CapabilityDefinition(
        entity.getId(),
        entity.getCode(),
        entity.getDisplayNameNl(),
        entity.getDescriptionNl(),
        entity.isActive()
    );
  }

  private UserAssignmentDefinition toAssignmentDefinition(UserAssignmentEntity entity) {
    return new UserAssignmentDefinition(
        entity.getId(),
        String.valueOf(entity.getUserId()),
        entity.getRoleCode(),
        entity.getScopeType(),
        entity.getBoardId(),
        entity.getSchoolId(),
        entity.getPermissionLevel(),
        entity.isActive(),
        entity.getValidFrom() != null ? entity.getValidFrom() : Instant.EPOCH,
        entity.getValidTo()
    );
  }
}
