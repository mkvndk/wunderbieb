package nl.wunderbieb.kms.insights.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "score_configurations")
public class ScoreConfigurationEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 100)
  private String code;

  @Column(name = "numeric_value", nullable = false)
  private int numericValue;

  @Column(name = "display_label_nl", nullable = false, length = 255)
  private String displayLabelNl;

  @Column(name = "description_nl", nullable = false, length = 500)
  private String descriptionNl;

  @Column(name = "sort_order", nullable = false)
  private int sortOrder;

  @Column(nullable = false)
  private boolean active;

  protected ScoreConfigurationEntity() {
  }

  public ScoreConfigurationEntity(
      String code,
      int numericValue,
      String displayLabelNl,
      String descriptionNl,
      int sortOrder,
      boolean active
  ) {
    this.code = code;
    this.numericValue = numericValue;
    this.displayLabelNl = displayLabelNl;
    this.descriptionNl = descriptionNl;
    this.sortOrder = sortOrder;
    this.active = active;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCode() {
    return code;
  }

  public int getNumericValue() {
    return numericValue;
  }

  public void setNumericValue(int numericValue) {
    this.numericValue = numericValue;
  }

  public String getDisplayLabelNl() {
    return displayLabelNl;
  }

  public void setDisplayLabelNl(String displayLabelNl) {
    this.displayLabelNl = displayLabelNl;
  }

  public String getDescriptionNl() {
    return descriptionNl;
  }

  public void setDescriptionNl(String descriptionNl) {
    this.descriptionNl = descriptionNl;
  }

  public int getSortOrder() {
    return sortOrder;
  }

  public void setSortOrder(int sortOrder) {
    this.sortOrder = sortOrder;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
}
