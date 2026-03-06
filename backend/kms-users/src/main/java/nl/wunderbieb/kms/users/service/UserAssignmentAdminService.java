package nl.wunderbieb.kms.users.service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import nl.wunderbieb.kms.audit.service.AuditService;
import nl.wunderbieb.kms.commons.access.ScopeType;
import nl.wunderbieb.kms.users.domain.Role;
import nl.wunderbieb.kms.users.domain.UserAssignment;
import nl.wunderbieb.kms.users.repository.UserAssignmentEntity;
import nl.wunderbieb.kms.users.repository.UserAssignmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserAssignmentAdminService {

  private final RoleAdminService roleAdminService;
  private final AuditService auditService;
  private final UserAssignmentRepository userAssignmentRepository;

  public UserAssignmentAdminService(
      RoleAdminService roleAdminService,
      AuditService auditService,
      UserAssignmentRepository userAssignmentRepository
  ) {
    this.roleAdminService = roleAdminService;
    this.auditService = auditService;
    this.userAssignmentRepository = userAssignmentRepository;
  }

  @Transactional(readOnly = true)
  public List<UserAssignment> getAssignments(long userId) {
    return userAssignmentRepository.findAllByUserIdOrderByIdAsc(userId).stream()
        .map(this::toDomain)
        .toList();
  }

  public UserAssignment createAssignment(
      String actorRoleCode,
      long userId,
      String roleCode,
      ScopeType scopeType,
      Long boardId,
      Long schoolId,
      Instant validFrom,
      Instant validTo
  ) {
    validateScope(scopeType, boardId, schoolId);
    Role role = roleAdminService.requireRoleByCode(roleCode);
    if (role.scopeType() != scopeType) {
      throw new IllegalArgumentException("Rolscope komt niet overeen met assignment scope.");
    }
    UserAssignmentEntity assignment = userAssignmentRepository.save(new UserAssignmentEntity(
        userId,
        role.code(),
        scopeType,
        boardId,
        schoolId,
        role.permissionLevel(),
        true,
        validFrom,
        validTo
    ));
    auditService.record("assignment_created", actorRoleCode, "assignment", String.valueOf(assignment.getId()), "Assignment aangemaakt");
    return toDomain(assignment);
  }

  public UserAssignment updateAssignment(
      String actorRoleCode,
      long assignmentId,
      String roleCode,
      Instant validFrom,
      Instant validTo,
      Boolean active
  ) {
    UserAssignmentEntity existing = userAssignmentRepository.findById(assignmentId)
        .orElseThrow(() -> new NoSuchElementException("Assignment niet gevonden."));
    if (roleCode != null) {
      Role role = roleAdminService.requireRoleByCode(roleCode);
      if (role.scopeType() != existing.getScopeType()) {
        throw new IllegalArgumentException("Nieuwe rol moet dezelfde scope hebben als de assignment.");
      }
      existing.setRoleCode(role.code());
      existing.setPermissionLevel(role.permissionLevel());
    }
    if (validFrom != null) {
      existing.setValidFrom(validFrom);
    }
    if (validTo != null) {
      existing.setValidTo(validTo);
    }
    if (active != null) {
      existing.setActive(active);
    }
    UserAssignmentEntity updated = userAssignmentRepository.save(existing);
    auditService.record("assignment_updated", actorRoleCode, "assignment", String.valueOf(updated.getId()), "Assignment bijgewerkt");
    return toDomain(updated);
  }

  private void validateScope(ScopeType scopeType, Long boardId, Long schoolId) {
    switch (scopeType) {
      case PLATFORM -> {
        if (boardId != null || schoolId != null) {
          throw new IllegalArgumentException("PLATFORM assignments mogen geen boardId of schoolId hebben.");
        }
      }
      case BOARD -> {
        if (boardId == null || schoolId != null) {
          throw new IllegalArgumentException("BOARD assignments vereisen boardId en geen schoolId.");
        }
      }
      case SCHOOL, EXTERN -> {
        if (schoolId == null) {
          throw new IllegalArgumentException(scopeType + " assignments vereisen schoolId.");
        }
      }
      default -> throw new IllegalStateException("Onbekende scope.");
    }
  }

  private UserAssignment toDomain(UserAssignmentEntity entity) {
    return new UserAssignment(
        entity.getId(),
        entity.getUserId(),
        entity.getRoleCode(),
        entity.getScopeType(),
        entity.getBoardId(),
        entity.getSchoolId(),
        entity.getPermissionLevel(),
        entity.isActive(),
        entity.getValidFrom(),
        entity.getValidTo()
    );
  }
}
