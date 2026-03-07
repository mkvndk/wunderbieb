package nl.wunderbieb.kms.api.security;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import nl.wunderbieb.kms.users.domain.Role;
import nl.wunderbieb.kms.users.repository.UserAssignmentEntity;
import nl.wunderbieb.kms.users.repository.UserAssignmentRepository;
import nl.wunderbieb.kms.users.service.RoleAdminService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OidcAccessContextService {

  private static final List<String> USER_ID_CLAIMS = List.of("user_id", "userId", "uid", "wb_user_id", "sub");
  private static final List<String> ROLE_CODE_CLAIMS = List.of("role_code", "roleCode");
  private static final List<String> ROLE_CODES_CLAIMS = List.of("role_codes", "roleCodes");
  private static final List<String> SCOPE_TYPE_CLAIMS = List.of("scope_type", "scopeType");
  private static final List<String> BOARD_ID_CLAIMS = List.of("board_id", "boardId");
  private static final List<String> SCHOOL_ID_CLAIMS = List.of("school_id", "schoolId");
  private static final List<String> ASSIGNMENT_ID_CLAIMS = List.of("assignment_id", "assignmentId");

  private final UserAssignmentRepository userAssignmentRepository;
  private final RoleAdminService roleAdminService;

  public OidcAccessContextService(UserAssignmentRepository userAssignmentRepository, RoleAdminService roleAdminService) {
    this.userAssignmentRepository = userAssignmentRepository;
    this.roleAdminService = roleAdminService;
  }

  public Optional<AccessContext> resolve(Jwt jwt) {
    Optional<Long> userId = resolveLongClaim(jwt, USER_ID_CLAIMS);
    if (userId.isEmpty()) {
      return fallbackFromTokenClaims(jwt);
    }

    List<UserAssignmentEntity> assignments = userAssignmentRepository.findAllByUserIdAndActiveTrueOrderByIdAsc(userId.get());
    if (assignments.isEmpty()) {
      return Optional.empty();
    }

    Optional<UserAssignmentEntity> assignment = selectAssignment(jwt, assignments);
    if (assignment.isEmpty()) {
      return Optional.empty();
    }

    Role role = roleAdminService.requireRoleByCode(assignment.get().getRoleCode());
    return Optional.of(new AccessContext(
        role.code(),
        assignment.get().getScopeType(),
        assignment.get().getPermissionLevel(),
        role.capabilityCodes()
    ));
  }

  private Optional<UserAssignmentEntity> selectAssignment(Jwt jwt, List<UserAssignmentEntity> assignments) {
    Optional<Long> assignmentId = resolveLongClaim(jwt, ASSIGNMENT_ID_CLAIMS);
    if (assignmentId.isPresent()) {
      return assignments.stream()
          .filter(assignment -> assignmentId.get().equals(assignment.getId()))
          .findFirst();
    }

    Optional<String> roleCode = resolveStringClaim(jwt, ROLE_CODE_CLAIMS);
    Optional<String> scopeType = resolveStringClaim(jwt, SCOPE_TYPE_CLAIMS);
    Optional<Long> boardId = resolveLongClaim(jwt, BOARD_ID_CLAIMS);
    Optional<Long> schoolId = resolveLongClaim(jwt, SCHOOL_ID_CLAIMS);

    if (roleCode.isPresent() || scopeType.isPresent() || boardId.isPresent() || schoolId.isPresent()) {
      List<UserAssignmentEntity> matches = assignments.stream()
          .filter(assignment -> roleCode.isEmpty() || roleCode.get().equalsIgnoreCase(assignment.getRoleCode()))
          .filter(assignment -> scopeType.isEmpty() || scopeType.get().equalsIgnoreCase(assignment.getScopeType().name()))
          .filter(assignment -> boardId.isEmpty() || boardId.get().equals(assignment.getBoardId()))
          .filter(assignment -> schoolId.isEmpty() || schoolId.get().equals(assignment.getSchoolId()))
          .toList();
      if (matches.size() == 1) {
        return Optional.of(matches.getFirst());
      }
    }

    Set<String> roleCodes = resolveStringCollectionClaim(jwt, ROLE_CODES_CLAIMS);
    if (!roleCodes.isEmpty()) {
      List<UserAssignmentEntity> matches = assignments.stream()
          .filter(assignment -> roleCodes.contains(assignment.getRoleCode()))
          .toList();
      if (matches.size() == 1) {
        return Optional.of(matches.getFirst());
      }
      return matches.stream()
          .filter(assignment -> "PLATFORM_ADMIN".equals(assignment.getRoleCode()))
          .findFirst();
    }

    if (assignments.size() == 1) {
      return Optional.of(assignments.getFirst());
    }

    return assignments.stream()
        .filter(assignment -> "PLATFORM_ADMIN".equals(assignment.getRoleCode()))
        .findFirst();
  }

  private Optional<AccessContext> fallbackFromTokenClaims(Jwt jwt) {
    Optional<String> roleCode = resolveStringClaim(jwt, ROLE_CODE_CLAIMS);
    Optional<String> scopeType = resolveStringClaim(jwt, SCOPE_TYPE_CLAIMS);
    if (roleCode.isEmpty() || scopeType.isEmpty()) {
      return Optional.empty();
    }

    Role role = roleAdminService.requireRoleByCode(roleCode.get());
    return Optional.of(new AccessContext(
        role.code(),
        role.scopeType(),
        role.permissionLevel(),
        role.capabilityCodes()
    ));
  }

  private Optional<String> resolveStringClaim(Jwt jwt, List<String> claimNames) {
    return claimNames.stream()
        .map(jwt::getClaimAsString)
        .filter(value -> value != null && !value.isBlank())
        .findFirst();
  }

  private Optional<Long> resolveLongClaim(Jwt jwt, List<String> claimNames) {
    for (String claimName : claimNames) {
      Object value = jwt.getClaims().get(claimName);
      if (value instanceof Number number) {
        return Optional.of(number.longValue());
      }
      if (value instanceof String stringValue && !stringValue.isBlank() && stringValue.chars().allMatch(Character::isDigit)) {
        return Optional.of(Long.parseLong(stringValue));
      }
    }
    return Optional.empty();
  }

  private Set<String> resolveStringCollectionClaim(Jwt jwt, List<String> claimNames) {
    for (String claimName : claimNames) {
      Object value = jwt.getClaims().get(claimName);
      if (value instanceof Collection<?> collection) {
        return collection.stream()
            .map(String::valueOf)
            .filter(entry -> !entry.isBlank())
            .map(String::trim)
            .collect(java.util.stream.Collectors.toSet());
      }
    }
    return Set.of();
  }
}
