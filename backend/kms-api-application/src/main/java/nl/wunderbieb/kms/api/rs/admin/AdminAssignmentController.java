package nl.wunderbieb.kms.api.rs.admin;

import jakarta.validation.Valid;
import java.util.List;
import nl.wunderbieb.kms.api.rs.admin.dto.AssignmentCreateRequest;
import nl.wunderbieb.kms.api.rs.admin.dto.AssignmentPatchRequest;
import nl.wunderbieb.kms.api.security.AccessContextResolver;
import nl.wunderbieb.kms.users.domain.UserAssignment;
import nl.wunderbieb.kms.users.service.UserAssignmentAdminService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminAssignmentController {

  private final UserAssignmentAdminService userAssignmentAdminService;
  private final AccessContextResolver accessContextResolver;

  public AdminAssignmentController(
      UserAssignmentAdminService userAssignmentAdminService,
      AccessContextResolver accessContextResolver
  ) {
    this.userAssignmentAdminService = userAssignmentAdminService;
    this.accessContextResolver = accessContextResolver;
  }

  @GetMapping("/users/{userId}/assignments")
  public List<UserAssignment> listAssignments(@PathVariable long userId) {
    accessContextResolver.requireCurrentContext().requireCapability("MANAGE_USERS");
    return userAssignmentAdminService.getAssignments(userId);
  }

  @PostMapping("/users/{userId}/assignments")
  public UserAssignment createAssignment(@PathVariable long userId, @Valid @RequestBody AssignmentCreateRequest request) {
    String actorRoleCode = accessContextResolver.requireCurrentContext().roleCode();
    accessContextResolver.requireCurrentContext().requireCapability("MANAGE_USERS");
    return userAssignmentAdminService.createAssignment(
        actorRoleCode,
        userId,
        request.roleCode(),
        request.scopeType(),
        request.boardId(),
        request.schoolId(),
        request.validFrom(),
        request.validTo()
    );
  }

  @PatchMapping("/assignments/{assignmentId}")
  public UserAssignment patchAssignment(@PathVariable long assignmentId, @RequestBody AssignmentPatchRequest request) {
    String actorRoleCode = accessContextResolver.requireCurrentContext().roleCode();
    accessContextResolver.requireCurrentContext().requireCapability("MANAGE_USERS");
    return userAssignmentAdminService.updateAssignment(
        actorRoleCode,
        assignmentId,
        request.roleCode(),
        request.validFrom(),
        request.validTo(),
        request.active()
    );
  }
}
