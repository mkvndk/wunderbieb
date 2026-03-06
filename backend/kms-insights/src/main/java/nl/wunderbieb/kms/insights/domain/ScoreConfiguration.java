package nl.wunderbieb.kms.insights.domain;

public record ScoreConfiguration(
    long id,
    String code,
    int numericValue,
    String displayLabelNl,
    String descriptionNl,
    int sortOrder,
    boolean active
) {

  public ScoreConfiguration withPatch(Integer numericValue, String displayLabelNl, String descriptionNl, Integer sortOrder, Boolean active) {
    return new ScoreConfiguration(
        id,
        code,
        numericValue != null ? numericValue : this.numericValue,
        displayLabelNl != null ? displayLabelNl : this.displayLabelNl,
        descriptionNl != null ? descriptionNl : this.descriptionNl,
        sortOrder != null ? sortOrder : this.sortOrder,
        active != null ? active : this.active
    );
  }
}
