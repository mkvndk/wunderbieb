package nl.wunderbieb.kms.docs.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentVersionRepository extends JpaRepository<DocumentVersionEntity, Long> {

  List<DocumentVersionEntity> findAllByDocumentIdOrderByVersionNumberDesc(long documentId);

  Optional<DocumentVersionEntity> findByDocumentIdAndVersionNumber(long documentId, int versionNumber);
}
