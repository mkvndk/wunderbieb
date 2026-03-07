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
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import nl.wunderbieb.kms.docs.domain.DocumentOnboardingStatus;

@Entity
@Table(
    name = "document_user_statuses",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_document_user_status_document_user", columnNames = {"document_id", "user_id"})
    }
)
public class DocumentUserStatusEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "document_id", nullable = false)
  private DocumentEntity document;

  @Column(name = "user_id", nullable = false)
  private long userId;

  @Enumerated(EnumType.STRING)
  @Column(name = "onboarding_status", nullable = false, length = 50)
  private DocumentOnboardingStatus onboardingStatus;

  @Column(name = "last_read_at")
  private Instant lastReadAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Column(name = "updated_by_user_id", nullable = false)
  private long updatedByUserId;

  protected DocumentUserStatusEntity() {
  }

  public DocumentUserStatusEntity(
      DocumentEntity document,
      long userId,
      DocumentOnboardingStatus onboardingStatus,
      Instant lastReadAt,
      Instant updatedAt,
      long updatedByUserId
  ) {
    this.document = document;
    this.userId = userId;
    this.onboardingStatus = onboardingStatus;
    this.lastReadAt = lastReadAt;
    this.updatedAt = updatedAt;
    this.updatedByUserId = updatedByUserId;
  }

  public Long getId() {
    return id;
  }

  public DocumentEntity getDocument() {
    return document;
  }

  public long getUserId() {
    return userId;
  }

  public DocumentOnboardingStatus getOnboardingStatus() {
    return onboardingStatus;
  }

  public void setOnboardingStatus(DocumentOnboardingStatus onboardingStatus) {
    this.onboardingStatus = onboardingStatus;
  }

  public Instant getLastReadAt() {
    return lastReadAt;
  }

  public void setLastReadAt(Instant lastReadAt) {
    this.lastReadAt = lastReadAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }

  public long getUpdatedByUserId() {
    return updatedByUserId;
  }

  public void setUpdatedByUserId(long updatedByUserId) {
    this.updatedByUserId = updatedByUserId;
  }
}
