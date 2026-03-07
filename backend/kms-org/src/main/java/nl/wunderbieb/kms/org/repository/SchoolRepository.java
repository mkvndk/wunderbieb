package nl.wunderbieb.kms.org.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolRepository extends JpaRepository<SchoolEntity, Long> {

  boolean existsByBrin(String brin);

  List<SchoolEntity> findAllByOrderByIdAsc();
}
