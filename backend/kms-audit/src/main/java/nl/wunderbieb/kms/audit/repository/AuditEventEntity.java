package nl.wunderbieb.kms.audit.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "audit_events")
public class AuditEventEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 150)
  private String action;

  @Column(name = "actor_role_code", nullable = false, length = 100)
  private String actorRoleCode;

  @Column(name = "entity_type", nullable = false, length = 100)
  private String entityType;

  @Column(name = "entity_id", nullable = false, length = 255)
  private String entityId;

  @Column(nullable = false, length = 1000)
  private String summary;

  @Column(name = "occurred_at", nullable = false)
  private Instant occurredAt;

  protected AuditEventEntity() {
  }

  public AuditEventEntity(
      String action,
      String actorRoleCode,
      String entityType,
      String entityId,
      String summary,
      Instant occurredAt
  ) {
    this.action = action;
    this.actorRoleCode = actorRoleCode;
    this.entityType = entityType;
    this.entityId = entityId;
    this.summary = summary;
    this.occurredAt = occurredAt;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAction() {
    return action;
  }

  public String getActorRoleCode() {
    return actorRoleCode;
  }

  public String getEntityType() {
    return entityType;
  }

  public String getEntityId() {
    return entityId;
  }

  public String getSummary() {
    return summary;
  }

  public Instant getOccurredAt() {
    return occurredAt;
  }
}
