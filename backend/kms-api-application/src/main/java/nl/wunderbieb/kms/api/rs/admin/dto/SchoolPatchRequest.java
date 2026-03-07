package nl.wunderbieb.kms.api.rs.admin.dto;

public record SchoolPatchRequest(
    String displayNameNl,
    Boolean active
) {
}
