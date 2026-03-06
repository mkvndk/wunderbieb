package nl.wunderbieb.kms.api.rs.admin.dto;

public record CapabilityPatchRequest(
    String displayNameNl,
    String descriptionNl,
    Boolean active
) {
}
