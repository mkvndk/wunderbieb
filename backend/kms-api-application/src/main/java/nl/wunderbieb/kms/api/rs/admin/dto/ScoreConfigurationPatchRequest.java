package nl.wunderbieb.kms.api.rs.admin.dto;

public record ScoreConfigurationPatchRequest(
    Integer numericValue,
    String displayLabelNl,
    String descriptionNl,
    Integer sortOrder,
    Boolean active
) {
}
