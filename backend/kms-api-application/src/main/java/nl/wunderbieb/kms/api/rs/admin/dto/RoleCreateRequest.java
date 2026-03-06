package nl.wunderbieb.kms.api.rs.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;
import nl.wunderbieb.kms.commons.access.PermissionLevel;
import nl.wunderbieb.kms.commons.access.ScopeType;

public record RoleCreateRequest(
    @NotBlank String code,
    @NotBlank String displayNameNl,
    @NotBlank String descriptionNl,
    ScopeType scopeType,
    PermissionLevel permissionLevel,
    @NotEmpty Set<String> capabilityCodes
) {
}
