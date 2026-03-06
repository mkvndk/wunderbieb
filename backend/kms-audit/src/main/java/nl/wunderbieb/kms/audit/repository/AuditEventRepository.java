package nl.wunderbieb.kms.audit.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditEventRepository extends JpaRepository<AuditEventEntity, Long> {

  List<AuditEventEntity> findAllByOrderByOccurredAtAscIdAsc();
}
