package nl.wunderbieb.kms.users.domain;

public record Capability(
    long id,
    String code,
    String displayNameNl,
    String descriptionNl,
    boolean active
) {

  public Capability withPatch(String displayNameNl, String descriptionNl, Boolean active) {
    return new Capability(
        id,
        code,
        displayNameNl != null ? displayNameNl : this.displayNameNl,
        descriptionNl != null ? descriptionNl : this.descriptionNl,
        active != null ? active : this.active
    );
  }
}
