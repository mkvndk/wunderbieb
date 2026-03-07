package nl.wunderbieb.kms.org.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

  boolean existsByCode(String code);

  List<BoardEntity> findAllByOrderByIdAsc();
}
