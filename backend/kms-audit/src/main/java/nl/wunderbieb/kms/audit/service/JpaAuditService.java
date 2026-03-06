package nl.wunderbieb.kms.audit.service;

import java.time.Instant;
import java.util.List;
import nl.wunderbieb.kms.audit.domain.AuditEvent;
import nl.wunderbieb.kms.audit.repository.AuditEventEntity;
import nl.wunderbieb.kms.audit.repository.AuditEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JpaAuditService implements AuditService {

  private final AuditEventRepository auditEventRepository;

  public JpaAuditService(AuditEventRepository auditEventRepository) {
    this.auditEventRepository = auditEventRepository;
  }

  @Override
  public AuditEvent record(String action, String actorRoleCode, String entityType, String entityId, String summary) {
    AuditEventEntity entity = auditEventRepository.save(new AuditEventEntity(
        action,
        actorRoleCode,
        entityType,
        entityId,
        summary,
        Instant.now()
    ));
    return toDomain(entity);
  }

  @Override
  @Transactional(readOnly = true)
  public List<AuditEvent> list() {
    return auditEventRepository.findAllByOrderByOccurredAtAscIdAsc().stream()
        .map(this::toDomain)
        .toList();
  }

  private AuditEvent toDomain(AuditEventEntity entity) {
    return new AuditEvent(
        entity.getId(),
        entity.getAction(),
        entity.getActorRoleCode(),
        entity.getEntityType(),
        entity.getEntityId(),
        entity.getSummary(),
        entity.getOccurredAt()
    );
  }
}
