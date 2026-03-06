package nl.wunderbieb.kms.taxonomy.domain;

public record DocumentTypeDefinition(
    long id,
    String code,
    String displayNameNl,
    String descriptionNl,
    boolean active,
    boolean requiredForOnboarding
) {
}
