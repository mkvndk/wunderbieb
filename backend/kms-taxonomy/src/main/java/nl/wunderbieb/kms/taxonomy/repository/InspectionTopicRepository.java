package nl.wunderbieb.kms.taxonomy.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InspectionTopicRepository extends JpaRepository<InspectionTopicEntity, Long> {

  boolean existsByCode(String code);

  Optional<InspectionTopicEntity> findByCode(String code);

  List<InspectionTopicEntity> findAllByOrderBySortOrderAscIdAsc();

  List<InspectionTopicEntity> findAllByDomainIdInOrderByDomainIdAscSortOrderAscIdAsc(List<Long> domainIds);
}
