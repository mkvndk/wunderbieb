package nl.wunderbieb.kms.docs.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import nl.wunderbieb.kms.commons.access.ScopeType;
import nl.wunderbieb.kms.docs.domain.DocumentWorkflowStatus;

@Entity
@Table(name = "documents")
public class DocumentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "document_type_code", nullable = false, length = 100)
  private String documentTypeCode;

  @Column(nullable = false, length = 255)
  private String title;

  @Column(length = 1000)
  private String summary;

  @Column(name = "source_reference", length = 255)
  private String sourceReference;

  @Column(name = "published_at")
  private Instant publishedAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "scope_type", nullable = false, length = 50)
  private ScopeType scopeType;

  @Column(name = "board_id")
  private Long boardId;

  @Column(name = "school_id")
  private Long schoolId;

  @Column(name = "created_by_user_id", nullable = false)
  private long createdByUserId;

  @Column(name = "active_version_number", nullable = false)
  private int activeVersionNumber;

  @Enumerated(EnumType.STRING)
  @Column(name = "workflow_status", nullable = false, length = 30)
  private DocumentWorkflowStatus workflowStatus;

  @Column(nullable = false)
  private boolean active;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  protected DocumentEntity() {
  }

  public DocumentEntity(
      String documentTypeCode,
      String title,
      String summary,
      String sourceReference,
      Instant publishedAt,
      ScopeType scopeType,
      Long boardId,
      Long schoolId,
      long createdByUserId,
      int activeVersionNumber,
      DocumentWorkflowStatus workflowStatus,
      boolean active,
      Instant createdAt,
      Instant updatedAt
  ) {
    this.documentTypeCode = documentTypeCode;
    this.title = title;
    this.summary = summary;
    this.sourceReference = sourceReference;
    this.publishedAt = publishedAt;
    this.scopeType = scopeType;
    this.boardId = boardId;
    this.schoolId = schoolId;
    this.createdByUserId = createdByUserId;
    this.activeVersionNumber = activeVersionNumber;
    this.workflowStatus = workflowStatus;
    this.active = active;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public Long getId() {
    return id;
  }

  public String getDocumentTypeCode() {
    return documentTypeCode;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getSummary() {
    return summary;
  }

  public String getSourceReference() {
    return sourceReference;
  }

  public Instant getPublishedAt() {
    return publishedAt;
  }

  public ScopeType getScopeType() {
    return scopeType;
  }

  public Long getBoardId() {
    return boardId;
  }

  public Long getSchoolId() {
    return schoolId;
  }

  public long getCreatedByUserId() {
    return createdByUserId;
  }

  public int getActiveVersionNumber() {
    return activeVersionNumber;
  }

  public void setActiveVersionNumber(int activeVersionNumber) {
    this.activeVersionNumber = activeVersionNumber;
  }

  public DocumentWorkflowStatus getWorkflowStatus() {
    return workflowStatus;
  }

  public void setWorkflowStatus(DocumentWorkflowStatus workflowStatus) {
    this.workflowStatus = workflowStatus;
  }

  public boolean isActive() {
    return active;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }
}
