package nl.wunderbieb.kms.org.domain;

public record BoardSchoolRelation(
    long id,
    long boardId,
    long schoolId,
    String relationType,
    boolean active
) {
}
