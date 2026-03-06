package nl.wunderbieb.kms.taxonomy.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "inspection_topics")
public class InspectionTopicEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "domain_id", nullable = false)
  private long domainId;

  @Column(nullable = false, unique = true, length = 100)
  private String code;

  @Column(name = "display_name_nl", nullable = false, length = 255)
  private String displayNameNl;

  @Column(name = "description_nl", nullable = false, length = 500)
  private String descriptionNl;

  @Column(name = "sort_order", nullable = false)
  private int sortOrder;

  @Column(nullable = false)
  private boolean active;

  protected InspectionTopicEntity() {
  }

  public InspectionTopicEntity(
      long domainId,
      String code,
      String displayNameNl,
      String descriptionNl,
      int sortOrder,
      boolean active
  ) {
    this.domainId = domainId;
    this.code = code;
    this.displayNameNl = displayNameNl;
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

  public long getDomainId() {
    return domainId;
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

  public int getSortOrder() {
    return sortOrder;
  }

  public boolean isActive() {
    return active;
  }
}
