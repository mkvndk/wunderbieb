package nl.wunderbieb.kms.users.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

  boolean existsByCode(String code);

  Optional<RoleEntity> findByCode(String code);

  List<RoleEntity> findAllByOrderByIdAsc();
}
