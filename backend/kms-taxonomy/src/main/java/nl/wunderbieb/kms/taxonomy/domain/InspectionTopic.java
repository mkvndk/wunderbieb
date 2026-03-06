package nl.wunderbieb.kms.taxonomy.domain;

public record InspectionTopic(
    long id,
    long domainId,
    String code,
    String displayNameNl,
    String descriptionNl,
    int sortOrder,
    boolean active
) {
}
