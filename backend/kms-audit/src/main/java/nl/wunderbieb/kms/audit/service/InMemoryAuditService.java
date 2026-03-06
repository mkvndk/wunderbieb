package nl.wunderbieb.kms.audit.service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import nl.wunderbieb.kms.audit.domain.AuditEvent;

public final class InMemoryAuditService implements AuditService {

  private final AtomicLong sequence = new AtomicLong(0);
  private final CopyOnWriteArrayList<AuditEvent> events = new CopyOnWriteArrayList<>();

  @Override
  public AuditEvent record(String action, String actorRoleCode, String entityType, String entityId, String summary) {
    AuditEvent event = new AuditEvent(
        sequence.incrementAndGet(),
        action,
        actorRoleCode,
        entityType,
        entityId,
        summary,
        Instant.now()
    );
    events.add(event);
    return event;
  }

  @Override
  public List<AuditEvent> list() {
    return List.copyOf(events);
  }
}
