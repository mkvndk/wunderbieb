package nl.wunderbieb.kms.insights.model;

public record ScoreConfigurationDefinition(
    long id,
    String code,
    int numericValue,
    String displayLabelNl,
    String descriptionNl,
    int sortOrder,
    boolean active
) {
}
