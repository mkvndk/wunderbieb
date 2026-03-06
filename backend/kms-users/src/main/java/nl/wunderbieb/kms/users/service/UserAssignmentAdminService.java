package nl.wunderbieb.kms.users.service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import nl.wunderbieb.kms.audit.service.AuditService;
import nl.wunderbieb.kms.commons.access.ScopeType;
import nl.wunderbieb.kms.users.domain.Role;
import nl.wunderbieb.kms.users.domain.UserAssignment;

public final class UserAssignmentAdminService {

  private final RoleAdminService roleAdminService;
  private final AuditService auditService;
  private final AtomicLong assignmentSequence = new AtomicLong(0);
  private final Map<Long, UserAssignment> assignmentsById = new ConcurrentHashMap<>();

  public UserAssignmentAdminService(RoleAdminService roleAdminService, AuditService auditService) {
    this.roleAdminService = roleAdminService;
    this.auditService = auditService;
  }

  public List<UserAssignment> getAssignments(long userId) {
    return assignmentsById.values().stream()
        .filter(assignment -> assignment.userId() == userId)
        .sorted((left, right) -> Long.compare(left.id(), right.id()))
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
    UserAssignment assignment = new UserAssignment(
        assignmentSequence.incrementAndGet(),
        userId,
        role.code(),
        scopeType,
        boardId,
        schoolId,
        role.permissionLevel(),
        true,
        validFrom,
        validTo
    );
    assignmentsById.put(assignment.id(), assignment);
    auditService.record("assignment_created", actorRoleCode, "assignment", String.valueOf(assignment.id()), "Assignment aangemaakt");
    return assignment;
  }

  public UserAssignment updateAssignment(
      String actorRoleCode,
      long assignmentId,
      String roleCode,
      Instant validFrom,
      Instant validTo,
      Boolean active
  ) {
    UserAssignment existing = assignmentsById.get(assignmentId);
    if (existing == null) {
      throw new NoSuchElementException("Assignment niet gevonden.");
    }
    if (roleCode != null) {
      roleAdminService.requireRoleByCode(roleCode);
    }
    UserAssignment updated = existing.withPatch(roleCode, validFrom, validTo, active);
    assignmentsById.put(assignmentId, updated);
    auditService.record("assignment_updated", actorRoleCode, "assignment", String.valueOf(updated.id()), "Assignment bijgewerkt");
    return updated;
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
}
