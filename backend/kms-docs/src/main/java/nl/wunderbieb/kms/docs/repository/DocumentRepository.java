package nl.wunderbieb.kms.docs.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {

  List<DocumentEntity> findTop100ByOrderByUpdatedAtDesc();
}
