package nl.wunderbieb.kms.taxonomy.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "document_type_definitions")
public class DocumentTypeDefinitionEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 100)
  private String code;

  @Column(name = "display_name_nl", nullable = false, length = 255)
  private String displayNameNl;

  @Column(name = "description_nl", nullable = false, length = 500)
  private String descriptionNl;

  @Column(nullable = false)
  private boolean active;

  @Column(name = "required_for_onboarding", nullable = false)
  private boolean requiredForOnboarding;

  protected DocumentTypeDefinitionEntity() {
  }

  public DocumentTypeDefinitionEntity(
      String code,
      String displayNameNl,
      String descriptionNl,
      boolean active,
      boolean requiredForOnboarding
  ) {
    this.code = code;
    this.displayNameNl = displayNameNl;
    this.descriptionNl = descriptionNl;
    this.active = active;
    this.requiredForOnboarding = requiredForOnboarding;
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

  public String getDisplayNameNl() {
    return displayNameNl;
  }

  public String getDescriptionNl() {
    return descriptionNl;
  }

  public boolean isActive() {
    return active;
  }

  public boolean isRequiredForOnboarding() {
    return requiredForOnboarding;
  }
}
