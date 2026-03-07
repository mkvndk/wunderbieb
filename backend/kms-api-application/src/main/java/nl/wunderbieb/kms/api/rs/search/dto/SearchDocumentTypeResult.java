package nl.wunderbieb.kms.api.rs.search.dto;

public record SearchDocumentTypeResult(
    long id,
    String code,
    String displayNameNl,
    String descriptionNl,
    boolean requiredForOnboarding
) {
}
