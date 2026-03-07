package nl.wunderbieb.kms.api.rs.insights.dto;

import java.util.List;

public record DomainScoresResponse(
    int trendPeriodDays,
    List<DomainScoreResult> domains
) {
}
