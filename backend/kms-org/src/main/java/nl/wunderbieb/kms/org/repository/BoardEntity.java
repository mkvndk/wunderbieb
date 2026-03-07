package nl.wunderbieb.kms.org.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "boards")
public class BoardEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 100)
  private String code;

  @Column(name = "display_name_nl", nullable = false, length = 255)
  private String displayNameNl;

  @Column(nullable = false)
  private boolean active;

  protected BoardEntity() {
  }

  public BoardEntity(String code, String displayNameNl, boolean active) {
    this.code = code;
    this.displayNameNl = displayNameNl;
    this.active = active;
  }

  public Long getId() {
    return id;
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

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
}
