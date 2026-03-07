package nl.wunderbieb.kms.api.rs.insights;

import nl.wunderbieb.kms.api.rs.insights.dto.DomainScoresResponse;
import nl.wunderbieb.kms.api.security.AccessContextResolver;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/insights")
public class InsightsController {

  private final AccessContextResolver accessContextResolver;
  private final InsightsAggregationService insightsAggregationService;

  public InsightsController(AccessContextResolver accessContextResolver, InsightsAggregationService insightsAggregationService) {
    this.accessContextResolver = accessContextResolver;
    this.insightsAggregationService = insightsAggregationService;
  }

  @GetMapping("/domain-scores")
  public DomainScoresResponse getDomainScores(@RequestParam(defaultValue = "30") int trendPeriodDays) {
    accessContextResolver.requireCurrentContext();
    return insightsAggregationService.aggregateDomainScores(trendPeriodDays);
  }
}
