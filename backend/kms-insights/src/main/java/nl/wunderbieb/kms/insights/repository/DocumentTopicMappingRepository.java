package nl.wunderbieb.kms.insights.repository;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentTopicMappingRepository extends JpaRepository<DocumentTopicMappingEntity, Long> {

  boolean existsByDocumentIdAndTopicId(long documentId, long topicId);

  List<DocumentTopicMappingEntity> findAllByDocumentIdIn(Collection<Long> documentIds);
}
