package nl.wunderbieb.kms.insights.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "document_topic_mappings")
public class DocumentTopicMappingEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "document_id", nullable = false)
  private long documentId;

  @Column(name = "topic_id", nullable = false)
  private long topicId;

  @Column(name = "mapping_source", nullable = false, length = 100)
  private String mappingSource;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "created_by_user_id", nullable = false)
  private long createdByUserId;

  protected DocumentTopicMappingEntity() {
  }

  public DocumentTopicMappingEntity(
      long documentId,
      long topicId,
      String mappingSource,
      Instant createdAt,
      long createdByUserId
  ) {
    this.documentId = documentId;
    this.topicId = topicId;
    this.mappingSource = mappingSource;
    this.createdAt = createdAt;
    this.createdByUserId = createdByUserId;
  }

  public Long getId() {
    return id;
  }

  public long getDocumentId() {
    return documentId;
  }

  public long getTopicId() {
    return topicId;
  }

  public String getMappingSource() {
    return mappingSource;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public long getCreatedByUserId() {
    return createdByUserId;
  }
}
