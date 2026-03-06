package nl.wunderbieb.kms.taxonomy.model;

public record InspectionTopicDefinition(
    long id,
    String code,
    String displayNameNl,
    String descriptionNl,
    int sortOrder,
    boolean active
) {
}
