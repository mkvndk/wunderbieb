package nl.wunderbieb.kms.api.rs.admin;

import jakarta.validation.Valid;
import java.util.List;
import nl.wunderbieb.kms.api.rs.admin.dto.RoleCreateRequest;
import nl.wunderbieb.kms.api.rs.admin.dto.RolePatchRequest;
import nl.wunderbieb.kms.api.security.AccessContextResolver;
import nl.wunderbieb.kms.users.domain.Role;
import nl.wunderbieb.kms.users.service.RoleAdminService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/roles")
public class AdminRoleController {

  private final RoleAdminService roleAdminService;
  private final AccessContextResolver accessContextResolver;

  public AdminRoleController(RoleAdminService roleAdminService, AccessContextResolver accessContextResolver) {
    this.roleAdminService = roleAdminService;
    this.accessContextResolver = accessContextResolver;
  }

  @GetMapping
  public List<Role> listRoles() {
    accessContextResolver.requireCurrentContext().requireCapability("MANAGE_USERS");
    return roleAdminService.getRoles();
  }

  @PostMapping
  public Role createRole(@Valid @RequestBody RoleCreateRequest request) {
    String actorRoleCode = accessContextResolver.requireCurrentContext().roleCode();
    accessContextResolver.requireCurrentContext().requireCapability("MANAGE_USERS");
    return roleAdminService.createRole(
        actorRoleCode,
        request.code(),
        request.displayNameNl(),
        request.descriptionNl(),
        request.scopeType(),
        request.permissionLevel(),
        request.capabilityCodes()
    );
  }

  @PatchMapping("/{roleId}")
  public Role patchRole(@PathVariable long roleId, @RequestBody RolePatchRequest request) {
    String actorRoleCode = accessContextResolver.requireCurrentContext().roleCode();
    accessContextResolver.requireCurrentContext().requireCapability("MANAGE_USERS");
    return roleAdminService.updateRole(actorRoleCode, roleId, request.displayNameNl(), request.descriptionNl(), request.active(), request.capabilityCodes());
  }
}
