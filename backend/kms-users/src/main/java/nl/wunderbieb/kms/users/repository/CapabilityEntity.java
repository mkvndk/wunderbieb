package nl.wunderbieb.kms.users.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "capabilities")
public class CapabilityEntity {

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

  protected CapabilityEntity() {
  }

  public CapabilityEntity(String code, String displayNameNl, String descriptionNl, boolean active) {
    this.code = code;
    this.displayNameNl = displayNameNl;
    this.descriptionNl = descriptionNl;
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

  public String getDisplayNameNl() {
    return displayNameNl;
  }

  public void setDisplayNameNl(String displayNameNl) {
    this.displayNameNl = displayNameNl;
  }

  public String getDescriptionNl() {
    return descriptionNl;
  }

  public void setDescriptionNl(String descriptionNl) {
    this.descriptionNl = descriptionNl;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
}
