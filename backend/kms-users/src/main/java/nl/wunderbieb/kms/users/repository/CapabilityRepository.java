package nl.wunderbieb.kms.users.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CapabilityRepository extends JpaRepository<CapabilityEntity, Long> {

  boolean existsByCode(String code);

  Optional<CapabilityEntity> findByCode(String code);

  List<CapabilityEntity> findAllByOrderByIdAsc();
}
