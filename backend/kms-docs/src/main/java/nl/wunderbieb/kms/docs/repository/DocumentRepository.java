package nl.wunderbieb.kms.docs.repository;

import java.util.List;
import nl.wunderbieb.kms.docs.domain.DocumentWorkflowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {

  List<DocumentEntity> findTop100ByOrderByUpdatedAtDesc();

  List<DocumentEntity> findAllByActiveTrueOrderByUpdatedAtDesc();

  @Query("""
      select d
      from DocumentEntity d
      where (:termLike is null
        or lower(d.title) like :termLike
        or lower(coalesce(d.summary, '')) like :termLike
        or lower(d.documentTypeCode) like :termLike)
        and (:documentTypeCode is null or d.documentTypeCode = :documentTypeCode)
        and (:workflowStatus is null or d.workflowStatus = :workflowStatus)
        and (:schoolId is null or d.schoolId = :schoolId)
      order by d.updatedAt desc
      """)
  List<DocumentEntity> searchDocuments(
      @Param("termLike") String termLike,
      @Param("documentTypeCode") String documentTypeCode,
      @Param("workflowStatus") DocumentWorkflowStatus workflowStatus,
      @Param("schoolId") Long schoolId
  );
}
