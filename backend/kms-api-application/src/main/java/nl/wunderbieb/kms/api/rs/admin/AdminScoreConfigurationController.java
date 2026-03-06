package nl.wunderbieb.kms.api.rs.admin;

import jakarta.validation.Valid;
import java.util.List;
import nl.wunderbieb.kms.api.rs.admin.dto.ScoreConfigurationCreateRequest;
import nl.wunderbieb.kms.api.rs.admin.dto.ScoreConfigurationPatchRequest;
import nl.wunderbieb.kms.api.security.AccessContextResolver;
import nl.wunderbieb.kms.insights.domain.ScoreConfiguration;
import nl.wunderbieb.kms.insights.service.ScoreConfigurationAdminService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/score-configurations")
public class AdminScoreConfigurationController {

  private final ScoreConfigurationAdminService scoreConfigurationAdminService;
  private final AccessContextResolver accessContextResolver;

  public AdminScoreConfigurationController(
      ScoreConfigurationAdminService scoreConfigurationAdminService,
      AccessContextResolver accessContextResolver
  ) {
    this.scoreConfigurationAdminService = scoreConfigurationAdminService;
    this.accessContextResolver = accessContextResolver;
  }

  @GetMapping
  public List<ScoreConfiguration> listScoreConfigurations() {
    accessContextResolver.requireCurrentContext().requireCapability("MANAGE_SCORE_CONFIGURATION");
    return scoreConfigurationAdminService.getScoreConfigurations();
  }

  @PostMapping
  public ScoreConfiguration createScoreConfiguration(@Valid @RequestBody ScoreConfigurationCreateRequest request) {
    String actorRoleCode = accessContextResolver.requireCurrentContext().roleCode();
    accessContextResolver.requireCurrentContext().requireCapability("MANAGE_SCORE_CONFIGURATION");
    return scoreConfigurationAdminService.createScoreConfiguration(
        actorRoleCode,
        request.code(),
        request.numericValue(),
        request.displayLabelNl(),
        request.descriptionNl(),
        request.sortOrder()
    );
  }

  @PatchMapping("/{scoreConfigurationId}")
  public ScoreConfiguration patchScoreConfiguration(
      @PathVariable long scoreConfigurationId,
      @RequestBody ScoreConfigurationPatchRequest request
  ) {
    String actorRoleCode = accessContextResolver.requireCurrentContext().roleCode();
    accessContextResolver.requireCurrentContext().requireCapability("MANAGE_SCORE_CONFIGURATION");
    return scoreConfigurationAdminService.updateScoreConfiguration(
        actorRoleCode,
        scoreConfigurationId,
        request.numericValue(),
        request.displayLabelNl(),
        request.descriptionNl(),
        request.sortOrder(),
        request.active()
    );
  }
}
