package nl.wunderbieb.kms.users.model;

public record CapabilityDefinition(
    long id,
    String code,
    String displayNameNl,
    String descriptionNl,
    boolean active
) {
}
