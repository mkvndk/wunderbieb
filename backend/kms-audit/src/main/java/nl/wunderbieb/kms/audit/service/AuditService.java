package nl.wunderbieb.kms.audit.service;

import java.util.List;
import java.util.Map;
import nl.wunderbieb.kms.audit.domain.AuditEvent;

public interface AuditService {

  AuditEvent record(String action, String actorRoleCode, String entityType, String entityId, String summary);

  List<AuditEvent> list();

  default void logAdminRead(String action, Map<String, Object> details) {
    record(action, "PLATFORM_ADMIN", "admin-read", action, details.toString());
  }
}
