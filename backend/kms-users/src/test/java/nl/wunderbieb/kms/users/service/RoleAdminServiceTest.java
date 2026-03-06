package nl.wunderbieb.kms.users.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import nl.wunderbieb.kms.audit.service.InMemoryAuditService;
import nl.wunderbieb.kms.commons.access.PermissionLevel;
import nl.wunderbieb.kms.commons.access.ScopeType;
import org.junit.jupiter.api.Test;

class RoleAdminServiceTest {

  @Test
  void createRole_keepsConfiguredCapabilities() {
    RoleAdminService service = new RoleAdminService(new InMemoryAuditService());

    var role = service.createRole(
        "PLATFORM_ADMIN",
        "EXTERN_AUDITOR",
        "Externe auditor",
        "Auditrol",
        ScopeType.EXTERN,
        PermissionLevel.READ,
        Set.of("EXPORT_DATA")
    );

    assertThat(role.capabilityCodes()).containsExactly("EXPORT_DATA");
    assertThat(service.requireRoleByCode("EXTERN_AUDITOR").scopeType()).isEqualTo(ScopeType.EXTERN);
  }
}
