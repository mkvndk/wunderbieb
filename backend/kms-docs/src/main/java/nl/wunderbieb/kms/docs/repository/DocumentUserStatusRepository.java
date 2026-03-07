package nl.wunderbieb.kms.docs.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentUserStatusRepository extends JpaRepository<DocumentUserStatusEntity, Long> {

  Optional<DocumentUserStatusEntity> findByDocumentIdAndUserId(long documentId, long userId);

  List<DocumentUserStatusEntity> findAllByUserIdAndDocumentIdIn(long userId, Collection<Long> documentIds);
}
