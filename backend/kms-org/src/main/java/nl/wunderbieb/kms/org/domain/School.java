package nl.wunderbieb.kms.org.domain;

public record School(
    long id,
    String brin,
    String displayNameNl,
    boolean active
) {
}
