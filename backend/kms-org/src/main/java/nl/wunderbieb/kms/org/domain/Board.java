package nl.wunderbieb.kms.org.domain;

public record Board(
    long id,
    String code,
    String displayNameNl,
    boolean active
) {
}
