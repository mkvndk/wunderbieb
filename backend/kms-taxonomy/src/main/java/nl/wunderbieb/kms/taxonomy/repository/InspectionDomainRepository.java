package nl.wunderbieb.kms.taxonomy.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InspectionDomainRepository extends JpaRepository<InspectionDomainEntity, Long> {

  boolean existsByCode(String code);

  Optional<InspectionDomainEntity> findByCode(String code);

  List<InspectionDomainEntity> findAllByOrderBySortOrderAscIdAsc();
}
