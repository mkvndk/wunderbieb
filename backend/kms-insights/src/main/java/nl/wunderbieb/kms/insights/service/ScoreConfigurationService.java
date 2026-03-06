package nl.wunderbieb.kms.insights.service;

import java.util.List;
import nl.wunderbieb.kms.insights.model.ScoreConfigurationDefinition;
import nl.wunderbieb.kms.insights.repository.ScoreConfigurationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ScoreConfigurationService {

  private final ScoreConfigurationRepository scoreConfigurationRepository;

  public ScoreConfigurationService(ScoreConfigurationRepository scoreConfigurationRepository) {
    this.scoreConfigurationRepository = scoreConfigurationRepository;
  }

  public List<ScoreConfigurationDefinition> findScoreConfigurations() {
    return scoreConfigurationRepository.findAllByOrderBySortOrderAscIdAsc().stream()
        .map(entity -> new ScoreConfigurationDefinition(
            entity.getId(),
            entity.getCode(),
            entity.getNumericValue(),
            entity.getDisplayLabelNl(),
            entity.getDescriptionNl(),
            entity.getSortOrder(),
            entity.isActive()
        ))
        .toList();
  }
}
