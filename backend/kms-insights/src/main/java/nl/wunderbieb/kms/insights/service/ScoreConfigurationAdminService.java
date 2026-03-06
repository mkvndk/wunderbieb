package nl.wunderbieb.kms.insights.service;

import java.util.List;
import java.util.NoSuchElementException;
import nl.wunderbieb.kms.audit.service.AuditService;
import nl.wunderbieb.kms.insights.domain.ScoreConfiguration;
import nl.wunderbieb.kms.insights.repository.ScoreConfigurationEntity;
import nl.wunderbieb.kms.insights.repository.ScoreConfigurationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ScoreConfigurationAdminService {

  private final AuditService auditService;
  private final ScoreConfigurationRepository scoreConfigurationRepository;

  public ScoreConfigurationAdminService(AuditService auditService, ScoreConfigurationRepository scoreConfigurationRepository) {
    this.auditService = auditService;
    this.scoreConfigurationRepository = scoreConfigurationRepository;
  }

  @Transactional(readOnly = true)
  public List<ScoreConfiguration> getScoreConfigurations() {
    return scoreConfigurationRepository.findAllByOrderBySortOrderAscIdAsc().stream()
        .map(this::toDomain)
        .toList();
  }

  public ScoreConfiguration createScoreConfiguration(
      String actorRoleCode,
      String code,
      int numericValue,
      String displayLabelNl,
      String descriptionNl,
      int sortOrder
  ) {
    if (scoreConfigurationRepository.existsByCode(code)) {
      throw new IllegalArgumentException("Scoreconfiguratiecode bestaat al.");
    }
    ScoreConfigurationEntity configuration = scoreConfigurationRepository.save(new ScoreConfigurationEntity(
        code,
        numericValue,
        displayLabelNl,
        descriptionNl,
        sortOrder,
        true
    ));
    auditService.record("score_configuration_created", actorRoleCode, "score_configuration", configuration.getCode(), "Scoreconfiguratie aangemaakt");
    return toDomain(configuration);
  }

  public ScoreConfiguration updateScoreConfiguration(
      String actorRoleCode,
      long id,
      Integer numericValue,
      String displayLabelNl,
      String descriptionNl,
      Integer sortOrder,
      Boolean active
  ) {
    ScoreConfigurationEntity existing = scoreConfigurationRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Scoreconfiguratie niet gevonden."));
    if (numericValue != null) {
      existing.setNumericValue(numericValue);
    }
    if (displayLabelNl != null) {
      existing.setDisplayLabelNl(displayLabelNl);
    }
    if (descriptionNl != null) {
      existing.setDescriptionNl(descriptionNl);
    }
    if (sortOrder != null) {
      existing.setSortOrder(sortOrder);
    }
    if (active != null) {
      existing.setActive(active);
    }
    ScoreConfigurationEntity updated = scoreConfigurationRepository.save(existing);
    auditService.record("score_configuration_updated", actorRoleCode, "score_configuration", updated.getCode(), "Scoreconfiguratie bijgewerkt");
    return toDomain(updated);
  }

  private ScoreConfiguration toDomain(ScoreConfigurationEntity entity) {
    return new ScoreConfiguration(
        entity.getId(),
        entity.getCode(),
        entity.getNumericValue(),
        entity.getDisplayLabelNl(),
        entity.getDescriptionNl(),
        entity.getSortOrder(),
        entity.isActive()
    );
  }
}
