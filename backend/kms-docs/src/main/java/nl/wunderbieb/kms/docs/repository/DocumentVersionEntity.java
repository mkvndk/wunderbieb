package nl.wunderbieb.kms.docs.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import nl.wunderbieb.kms.docs.domain.DocumentVersionStatus;

@Entity
@Table(name = "document_versions")
public class DocumentVersionEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "document_id", nullable = false)
  private DocumentEntity document;

  @Column(name = "version_number", nullable = false)
  private int versionNumber;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private DocumentVersionStatus status;

  @Column(name = "content_json", nullable = false, length = 20000)
  private String contentJson;

  @Column(name = "change_summary", length = 500)
  private String changeSummary;

  @Column(name = "created_by_user_id", nullable = false)
  private long createdByUserId;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  protected DocumentVersionEntity() {
  }

  public DocumentVersionEntity(
      DocumentEntity document,
      int versionNumber,
      DocumentVersionStatus status,
      String contentJson,
      String changeSummary,
      long createdByUserId,
      Instant createdAt
  ) {
    this.document = document;
    this.versionNumber = versionNumber;
    this.status = status;
    this.contentJson = contentJson;
    this.changeSummary = changeSummary;
    this.createdByUserId = createdByUserId;
    this.createdAt = createdAt;
  }

  public Long getId() {
    return id;
  }

  public DocumentEntity getDocument() {
    return document;
  }

  public int getVersionNumber() {
    return versionNumber;
  }

  public DocumentVersionStatus getStatus() {
    return status;
  }

  public void setStatus(DocumentVersionStatus status) {
    this.status = status;
  }

  public String getContentJson() {
    return contentJson;
  }

  public String getChangeSummary() {
    return changeSummary;
  }

  public long getCreatedByUserId() {
    return createdByUserId;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
