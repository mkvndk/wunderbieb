package nl.wunderbieb.kms.api.rs.search.dto;

public record SearchTopicResult(
    long id,
    String code,
    String displayNameNl,
    String descriptionNl,
    String domainCode,
    String domainDisplayNameNl
) {
}
