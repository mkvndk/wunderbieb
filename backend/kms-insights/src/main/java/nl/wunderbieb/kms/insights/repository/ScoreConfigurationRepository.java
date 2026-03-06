package nl.wunderbieb.kms.insights.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreConfigurationRepository extends JpaRepository<ScoreConfigurationEntity, Long> {

  boolean existsByCode(String code);

  List<ScoreConfigurationEntity> findAllByOrderBySortOrderAscIdAsc();
}
