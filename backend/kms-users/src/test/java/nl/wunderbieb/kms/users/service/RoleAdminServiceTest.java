package nl.wunderbieb.kms.users.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Set;
import nl.wunderbieb.kms.audit.service.InMemoryAuditService;
import nl.wunderbieb.kms.commons.access.PermissionLevel;
import nl.wunderbieb.kms.commons.access.ScopeType;
import nl.wunderbieb.kms.users.repository.CapabilityRepository;
import nl.wunderbieb.kms.users.repository.RoleEntity;
import nl.wunderbieb.kms.users.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RoleAdminServiceTest {

  @Mock
  private RoleRepository roleRepository;

  @Mock
  private CapabilityRepository capabilityRepository;

  @Test
  void createRole_keepsConfiguredCapabilities() {
    when(capabilityRepository.existsByCode("EXPORT_DATA")).thenReturn(true);
    when(roleRepository.save(any(RoleEntity.class))).thenAnswer(invocation -> {
      RoleEntity entity = invocation.getArgument(0);
      RoleEntity saved = new RoleEntity(
          entity.getCode(),
          entity.getDisplayNameNl(),
          entity.getDescriptionNl(),
          entity.getScopeType(),
          entity.getPermissionLevel(),
          entity.isSystemRole(),
          entity.isActive(),
          entity.getCapabilityCodes()
      );
      saved.setId(101L);
      return saved;
    });
    when(roleRepository.findByCode("EXTERN_AUDITOR")).thenReturn(java.util.Optional.of(roleEntity()));

    RoleAdminService service = new RoleAdminService(new InMemoryAuditService(), roleRepository, capabilityRepository);

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

  private RoleEntity roleEntity() {
    RoleEntity entity = new RoleEntity(
        "EXTERN_AUDITOR",
        "Externe auditor",
        "Auditrol",
        ScopeType.EXTERN,
        PermissionLevel.READ,
        false,
        true,
        Set.of("EXPORT_DATA")
    );
    entity.setId(101L);
    return entity;
  }
}
