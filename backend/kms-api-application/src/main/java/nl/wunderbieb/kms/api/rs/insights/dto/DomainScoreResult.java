package nl.wunderbieb.kms.api.rs.insights.dto;

import java.time.Instant;
import java.util.List;

public record DomainScoreResult(
    long domainId,
    String domainCode,
    String domainDisplayNameNl,
    String scoreConfigurationCode,
    int scoreNumericValue,
    String scoreLabelNl,
    TrendDirection trendDirection,
    int trendPeriodDays,
    Instant latestActualizationDate,
    int supportingDocumentCount,
    List<DomainScoreEvidence> supportingDocuments
) {
}
