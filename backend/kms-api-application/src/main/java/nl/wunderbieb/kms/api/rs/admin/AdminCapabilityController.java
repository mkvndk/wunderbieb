package nl.wunderbieb.kms.api.rs.admin;

import jakarta.validation.Valid;
import java.util.List;
import nl.wunderbieb.kms.api.rs.admin.dto.CapabilityCreateRequest;
import nl.wunderbieb.kms.api.rs.admin.dto.CapabilityPatchRequest;
import nl.wunderbieb.kms.api.security.AccessContextResolver;
import nl.wunderbieb.kms.users.domain.Capability;
import nl.wunderbieb.kms.users.service.RoleAdminService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/capabilities")
public class AdminCapabilityController {

  private final RoleAdminService roleAdminService;
  private final AccessContextResolver accessContextResolver;

  public AdminCapabilityController(RoleAdminService roleAdminService, AccessContextResolver accessContextResolver) {
    this.roleAdminService = roleAdminService;
    this.accessContextResolver = accessContextResolver;
  }

  @GetMapping
  public List<Capability> listCapabilities() {
    accessContextResolver.requireCurrentContext().requireCapability("MANAGE_USERS");
    return roleAdminService.getCapabilities();
  }

  @PostMapping
  public Capability createCapability(@Valid @RequestBody CapabilityCreateRequest request) {
    String actorRoleCode = accessContextResolver.requireCurrentContext().roleCode();
    accessContextResolver.requireCurrentContext().requireCapability("MANAGE_USERS");
    return roleAdminService.createCapability(actorRoleCode, request.code(), request.displayNameNl(), request.descriptionNl());
  }

  @PatchMapping("/{capabilityId}")
  public Capability patchCapability(@PathVariable long capabilityId, @RequestBody CapabilityPatchRequest request) {
    String actorRoleCode = accessContextResolver.requireCurrentContext().roleCode();
    accessContextResolver.requireCurrentContext().requireCapability("MANAGE_USERS");
    return roleAdminService.updateCapability(actorRoleCode, capabilityId, request.displayNameNl(), request.descriptionNl(), request.active());
  }
}
