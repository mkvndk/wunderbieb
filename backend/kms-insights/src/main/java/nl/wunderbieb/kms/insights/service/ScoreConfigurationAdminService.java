package nl.wunderbieb.kms.insights.service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import nl.wunderbieb.kms.audit.service.AuditService;
import nl.wunderbieb.kms.insights.domain.ScoreConfiguration;

public final class ScoreConfigurationAdminService {

  private final AuditService auditService;
  private final AtomicLong sequence = new AtomicLong(0);
  private final Map<Long, ScoreConfiguration> configurationsById = new ConcurrentHashMap<>();

  public ScoreConfigurationAdminService(AuditService auditService) {
    this.auditService = auditService;
    seedDefaults();
  }

  public List<ScoreConfiguration> getScoreConfigurations() {
    return configurationsById.values().stream()
        .sorted((left, right) -> Integer.compare(left.sortOrder(), right.sortOrder()))
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
    ScoreConfiguration configuration = new ScoreConfiguration(
        sequence.incrementAndGet(),
        code,
        numericValue,
        displayLabelNl,
        descriptionNl,
        sortOrder,
        true
    );
    configurationsById.put(configuration.id(), configuration);
    auditService.record("score_configuration_created", actorRoleCode, "score_configuration", configuration.code(), "Scoreconfiguratie aangemaakt");
    return configuration;
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
    ScoreConfiguration existing = configurationsById.get(id);
    if (existing == null) {
      throw new NoSuchElementException("Scoreconfiguratie niet gevonden.");
    }
    ScoreConfiguration updated = existing.withPatch(numericValue, displayLabelNl, descriptionNl, sortOrder, active);
    configurationsById.put(id, updated);
    auditService.record("score_configuration_updated", actorRoleCode, "score_configuration", updated.code(), "Scoreconfiguratie bijgewerkt");
    return updated;
  }

  private void seedDefaults() {
    configurationsById.put(1L, new ScoreConfiguration(1L, "HIGH", 9, "Sterk op orde", "Voorlopige hoge score", 1, true));
    configurationsById.put(2L, new ScoreConfiguration(2L, "MEDIUM", 6, "Basis op orde", "Voorlopige middenscore", 2, true));
    configurationsById.put(3L, new ScoreConfiguration(3L, "LOW", 3, "Onvoldoende op orde", "Voorlopige lage score", 3, true));
    sequence.set(3);
  }
}
