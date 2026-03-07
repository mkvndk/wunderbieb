package nl.wunderbieb.kms.users.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAssignmentRepository extends JpaRepository<UserAssignmentEntity, Long> {

  List<UserAssignmentEntity> findAllByUserIdOrderByIdAsc(long userId);

  List<UserAssignmentEntity> findAllByUserIdAndActiveTrueOrderByIdAsc(long userId);
}
