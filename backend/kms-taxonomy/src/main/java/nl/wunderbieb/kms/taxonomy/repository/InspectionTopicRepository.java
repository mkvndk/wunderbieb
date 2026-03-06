package nl.wunderbieb.kms.taxonomy.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InspectionTopicRepository extends JpaRepository<InspectionTopicEntity, Long> {

  boolean existsByCode(String code);

  List<InspectionTopicEntity> findAllByOrderBySortOrderAscIdAsc();

  List<InspectionTopicEntity> findAllByDomainIdInOrderByDomainIdAscSortOrderAscIdAsc(List<Long> domainIds);
}
