package nl.wunderbieb.kms.org.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardSchoolRelationRepository extends JpaRepository<BoardSchoolRelationEntity, Long> {

  boolean existsByBoardIdAndSchoolIdAndRelationType(long boardId, long schoolId, String relationType);

  List<BoardSchoolRelationEntity> findAllByOrderByIdAsc();
}
