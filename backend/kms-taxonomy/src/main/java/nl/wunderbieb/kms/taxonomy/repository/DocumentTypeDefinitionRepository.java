package nl.wunderbieb.kms.taxonomy.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentTypeDefinitionRepository extends JpaRepository<DocumentTypeDefinitionEntity, Long> {

  boolean existsByCode(String code);

  List<DocumentTypeDefinitionEntity> findAllByOrderByIdAsc();
}
