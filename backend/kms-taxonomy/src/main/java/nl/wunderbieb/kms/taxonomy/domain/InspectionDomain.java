package nl.wunderbieb.kms.taxonomy.domain;

public record InspectionDomain(
    long id,
    String code,
    String displayNameNl,
    String descriptionNl,
    int sortOrder,
    boolean active
) {
}
