package nl.wunderbieb.kms.insights.service;

import java.util.List;
import nl.wunderbieb.kms.insights.model.ScoreConfigurationDefinition;
import org.springframework.stereotype.Service;

@Service
public class ScoreConfigurationService {

  private static final List<ScoreConfigurationDefinition> SCORE_CONFIGURATIONS = List.of(
      new ScoreConfigurationDefinition(1L, "HIGH", 9, "Sterk op orde", "Voorlopige hoge score", 1, true),
      new ScoreConfigurationDefinition(2L, "MEDIUM", 6, "Basis op orde", "Voorlopige middenscore", 2, true),
      new ScoreConfigurationDefinition(3L, "LOW", 3, "Onvoldoende op orde", "Voorlopige lage score", 3, true)
  );

  public List<ScoreConfigurationDefinition> findScoreConfigurations() {
    return SCORE_CONFIGURATIONS;
  }
}
