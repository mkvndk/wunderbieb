package nl.wunderbieb.kms.api.rs.admin.dto;

import java.util.Set;

public record RolePatchRequest(
    String displayNameNl,
    String descriptionNl,
    Boolean active,
    Set<String> capabilityCodes
) {
}
