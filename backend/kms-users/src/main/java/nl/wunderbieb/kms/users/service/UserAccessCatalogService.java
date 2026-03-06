package nl.wunderbieb.kms.users.service;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import nl.wunderbieb.kms.commons.access.PermissionLevel;
import nl.wunderbieb.kms.commons.access.ScopeType;
import nl.wunderbieb.kms.users.model.CapabilityDefinition;
import nl.wunderbieb.kms.users.model.RoleDefinition;
import nl.wunderbieb.kms.users.model.UserAssignmentDefinition;
import org.springframework.stereotype.Service;

@Service
public class UserAccessCatalogService {

  private static final List<CapabilityDefinition> CAPABILITIES = List.of(
      new CapabilityDefinition(1L, "MANAGE_ORG", "Organisatie beheren", "Mag besturen en scholen beheren", true),
      new CapabilityDefinition(2L, "MANAGE_USERS", "Gebruikers beheren", "Mag gebruikers en assignments beheren", true),
      new CapabilityDefinition(3L, "MANAGE_TAXONOMY", "Taxonomie beheren", "Mag documenttypen en domeinen beheren", true),
      new CapabilityDefinition(4L, "MANAGE_SCORE_CONFIGURATION", "Scoreconfiguratie beheren", "Mag scorewaarden beheren", true),
      new CapabilityDefinition(5L, "APPROVE_DOCUMENT", "Document goedkeuren", "Mag documenten goedkeuren", true)
  );

  private static final List<RoleDefinition> ROLES = List.of(
      new RoleDefinition(
          1L,
          "PLATFORM_ADMIN",
          "Super admin",
          "Platformbeheerder met volledige beheerrechten",
          ScopeType.PLATFORM,
          PermissionLevel.WRITE,
          true,
          true,
          Set.of("MANAGE_ORG", "MANAGE_USERS", "MANAGE_TAXONOMY", "MANAGE_SCORE_CONFIGURATION")
      ),
      new RoleDefinition(
          2L,
          "DIRECTEUR",
          "Directeur",
          "Schoolrol met beheer- en goedkeuringsrechten",
          ScopeType.SCHOOL,
          PermissionLevel.WRITE,
          true,
          true,
          Set.of("MANAGE_USERS", "APPROVE_DOCUMENT")
      ),
      new RoleDefinition(
          3L,
          "ONDERWIJSADVISEUR",
          "Onderwijsadviseur",
          "Externe rol met schoolscope en apart datafilter",
          ScopeType.EXTERN,
          PermissionLevel.WRITE,
          true,
          true,
          Set.of()
      )
  );

  private static final List<UserAssignmentDefinition> ASSIGNMENTS = List.of(
      new UserAssignmentDefinition(
          1L,
          "super-admin@wunderbieb.nl",
          "PLATFORM_ADMIN",
          ScopeType.PLATFORM,
          null,
          null,
          PermissionLevel.WRITE,
          true,
          Instant.parse("2026-03-05T00:00:00Z"),
          null
      ),
      new UserAssignmentDefinition(
          2L,
          "directeur@voorbeeldschool.nl",
          "DIRECTEUR",
          ScopeType.SCHOOL,
          12L,
          42L,
          PermissionLevel.WRITE,
          true,
          Instant.parse("2026-03-05T00:00:00Z"),
          null
      )
  );

  public List<RoleDefinition> findRoles(String scopeType, Boolean active) {
    return ROLES.stream()
        .filter(role -> scopeType == null || role.scopeType().name().equalsIgnoreCase(scopeType))
        .filter(role -> active == null || role.active() == active)
        .toList();
  }

  public List<CapabilityDefinition> findCapabilities() {
    return CAPABILITIES;
  }

  public List<UserAssignmentDefinition> findAssignments(String userId) {
    String normalizedUserId = userId.toLowerCase(Locale.ROOT);
    return ASSIGNMENTS.stream()
        .filter(assignment -> assignment.userId().toLowerCase(Locale.ROOT).equals(normalizedUserId))
        .toList();
  }
}
