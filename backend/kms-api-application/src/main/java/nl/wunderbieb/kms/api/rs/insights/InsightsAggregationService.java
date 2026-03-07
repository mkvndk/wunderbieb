package nl.wunderbieb.kms.api.rs.insights;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import nl.wunderbieb.kms.api.rs.admin.dto.DocumentTopicMappingResponse;
import nl.wunderbieb.kms.api.rs.insights.dto.DomainScoreEvidence;
import nl.wunderbieb.kms.api.rs.insights.dto.DomainScoreResult;
import nl.wunderbieb.kms.api.rs.insights.dto.DomainScoresResponse;
import nl.wunderbieb.kms.api.rs.insights.dto.TrendDirection;
import nl.wunderbieb.kms.audit.service.AuditService;
import nl.wunderbieb.kms.docs.domain.DocumentWorkflowStatus;
import nl.wunderbieb.kms.docs.repository.DocumentEntity;
import nl.wunderbieb.kms.docs.repository.DocumentRepository;
import nl.wunderbieb.kms.insights.repository.DocumentTopicMappingEntity;
import nl.wunderbieb.kms.insights.repository.DocumentTopicMappingRepository;
import nl.wunderbieb.kms.insights.repository.ScoreConfigurationEntity;
import nl.wunderbieb.kms.insights.repository.ScoreConfigurationRepository;
import nl.wunderbieb.kms.taxonomy.repository.InspectionDomainEntity;
import nl.wunderbieb.kms.taxonomy.repository.InspectionDomainRepository;
import nl.wunderbieb.kms.taxonomy.repository.InspectionTopicEntity;
import nl.wunderbieb.kms.taxonomy.repository.InspectionTopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InsightsAggregationService {

  private final AuditService auditService;
  private final DocumentRepository documentRepository;
  private final InspectionTopicRepository inspectionTopicRepository;
  private final InspectionDomainRepository inspectionDomainRepository;
  private final DocumentTopicMappingRepository documentTopicMappingRepository;
  private final ScoreConfigurationRepository scoreConfigurationRepository;

  public InsightsAggregationService(
      AuditService auditService,
      DocumentRepository documentRepository,
      InspectionTopicRepository inspectionTopicRepository,
      InspectionDomainRepository inspectionDomainRepository,
      DocumentTopicMappingRepository documentTopicMappingRepository,
      ScoreConfigurationRepository scoreConfigurationRepository
  ) {
    this.auditService = auditService;
    this.documentRepository = documentRepository;
    this.inspectionTopicRepository = inspectionTopicRepository;
    this.inspectionDomainRepository = inspectionDomainRepository;
    this.documentTopicMappingRepository = documentTopicMappingRepository;
    this.scoreConfigurationRepository = scoreConfigurationRepository;
  }

  public DocumentTopicMappingResponse createDocumentTopicMapping(
      String actorRoleCode,
      long actorUserId,
      long documentId,
      String topicCode,
      String mappingSource
  ) {
    DocumentEntity document = documentRepository.findById(documentId)
        .orElseThrow(() -> new NoSuchElementException("Document niet gevonden."));
    InspectionTopicEntity topic = inspectionTopicRepository.findByCode(topicCode)
        .orElseThrow(() -> new NoSuchElementException("Inspectieonderwerp niet gevonden."));
    if (documentTopicMappingRepository.existsByDocumentIdAndTopicId(documentId, topic.getId())) {
      throw new IllegalArgumentException("Document is al gekoppeld aan dit onderwerp.");
    }
    Instant now = Instant.now();
    DocumentTopicMappingEntity mapping = documentTopicMappingRepository.save(new DocumentTopicMappingEntity(
        document.getId(),
        topic.getId(),
        mappingSource,
        now,
        actorUserId
    ));
    auditService.record(
        "document_topic_mapping_created",
        actorRoleCode,
        "document_topic_mapping",
        String.valueOf(mapping.getId()),
        "Document gekoppeld aan onderwerp " + topicCode
    );
    return new DocumentTopicMappingResponse(
        mapping.getId(),
        mapping.getDocumentId(),
        mapping.getTopicId(),
        topic.getCode(),
        mapping.getMappingSource(),
        mapping.getCreatedAt(),
        mapping.getCreatedByUserId()
    );
  }

  @Transactional(readOnly = true)
  public DomainScoresResponse aggregateDomainScores(int trendPeriodDays) {
    if (trendPeriodDays < 1 || trendPeriodDays > 365) {
      throw new IllegalArgumentException("trendPeriodDays moet tussen 1 en 365 liggen.");
    }

    List<InspectionDomainEntity> domains = inspectionDomainRepository.findAllByOrderBySortOrderAscIdAsc().stream()
        .filter(InspectionDomainEntity::isActive)
        .toList();
    if (domains.isEmpty()) {
      return new DomainScoresResponse(trendPeriodDays, List.of());
    }

    Map<Long, InspectionTopicEntity> topicsById = inspectionTopicRepository.findAllByOrderBySortOrderAscIdAsc().stream()
        .filter(InspectionTopicEntity::isActive)
        .collect(LinkedHashMap::new, (map, topic) -> map.put(topic.getId(), topic), Map::putAll);

    List<DocumentEntity> documents = documentRepository.findAllByActiveTrueOrderByUpdatedAtDesc();
    if (documents.isEmpty()) {
      return new DomainScoresResponse(trendPeriodDays, List.of());
    }
    Map<Long, DocumentEntity> documentsById = documents.stream()
        .collect(LinkedHashMap::new, (map, doc) -> map.put(doc.getId(), doc), Map::putAll);
    List<DocumentTopicMappingEntity> mappings = documentTopicMappingRepository.findAllByDocumentIdIn(documentsById.keySet());

    Map<Long, Set<Long>> domainDocumentIds = new HashMap<>();
    for (DocumentTopicMappingEntity mapping : mappings) {
      InspectionTopicEntity topic = topicsById.get(mapping.getTopicId());
      if (topic == null) {
        continue;
      }
      if (!documentsById.containsKey(mapping.getDocumentId())) {
        continue;
      }
      domainDocumentIds.computeIfAbsent(topic.getDomainId(), ignored -> new LinkedHashSet<>()).add(mapping.getDocumentId());
    }

    List<ScoreConfigurationEntity> activeScores = scoreConfigurationRepository.findAllByOrderBySortOrderAscIdAsc().stream()
        .filter(ScoreConfigurationEntity::isActive)
        .toList();
    if (activeScores.isEmpty()) {
      throw new IllegalStateException("Geen actieve scoreconfiguraties beschikbaar.");
    }

    Instant now = Instant.now();
    Instant recentStart = now.minus(trendPeriodDays, ChronoUnit.DAYS);
    Instant previousStart = now.minus((long) trendPeriodDays * 2, ChronoUnit.DAYS);
    List<DomainScoreResult> results = new ArrayList<>();

    for (InspectionDomainEntity domain : domains) {
      Set<Long> docIds = domainDocumentIds.getOrDefault(domain.getId(), Set.of());
      if (docIds.isEmpty()) {
        continue;
      }
      List<DocumentEntity> supportingDocs = docIds.stream()
          .map(documentsById::get)
          .filter(java.util.Objects::nonNull)
          .sorted(Comparator.comparing(DocumentEntity::getUpdatedAt).reversed())
          .toList();
      if (supportingDocs.isEmpty()) {
        continue;
      }

      long approvedCount = supportingDocs.stream()
          .filter(doc -> doc.getWorkflowStatus() == DocumentWorkflowStatus.APPROVED)
          .count();
      long recentApprovedCount = supportingDocs.stream()
          .filter(doc -> doc.getWorkflowStatus() == DocumentWorkflowStatus.APPROVED)
          .filter(doc -> !doc.getUpdatedAt().isBefore(recentStart))
          .count();

      ScoreConfigurationEntity score = chooseScore(activeScores, approvedCount, recentApprovedCount);

      long recentCount = supportingDocs.stream()
          .filter(doc -> !doc.getUpdatedAt().isBefore(recentStart))
          .count();
      long previousCount = supportingDocs.stream()
          .filter(doc -> !doc.getUpdatedAt().isBefore(previousStart) && doc.getUpdatedAt().isBefore(recentStart))
          .count();
      TrendDirection trendDirection = recentCount > previousCount
          ? TrendDirection.UP
          : recentCount < previousCount ? TrendDirection.DOWN : TrendDirection.STABLE;

      Instant latestActualization = supportingDocs.stream()
          .map(DocumentEntity::getUpdatedAt)
          .max(Comparator.naturalOrder())
          .orElseThrow();
      List<DomainScoreEvidence> evidence = supportingDocs.stream()
          .limit(5)
          .map(doc -> new DomainScoreEvidence(
              doc.getId(),
              doc.getTitle(),
              doc.getWorkflowStatus().name(),
              doc.getUpdatedAt()
          ))
          .toList();

      results.add(new DomainScoreResult(
          domain.getId(),
          domain.getCode(),
          domain.getDisplayNameNl(),
          score.getCode(),
          score.getNumericValue(),
          score.getDisplayLabelNl(),
          trendDirection,
          trendPeriodDays,
          latestActualization,
          supportingDocs.size(),
          evidence
      ));
    }

    return new DomainScoresResponse(trendPeriodDays, results);
  }

  private ScoreConfigurationEntity chooseScore(List<ScoreConfigurationEntity> activeScores, long approvedCount, long recentApprovedCount) {
    List<ScoreConfigurationEntity> sorted = activeScores.stream()
        .sorted(Comparator.comparingInt(ScoreConfigurationEntity::getNumericValue))
        .toList();
    ScoreConfigurationEntity low = sorted.getFirst();
    ScoreConfigurationEntity medium = sorted.get(sorted.size() / 2);
    ScoreConfigurationEntity high = sorted.getLast();
    if (approvedCount == 0) {
      return low;
    }
    if (approvedCount >= 2 && recentApprovedCount >= 1) {
      return high;
    }
    return medium;
  }
}
