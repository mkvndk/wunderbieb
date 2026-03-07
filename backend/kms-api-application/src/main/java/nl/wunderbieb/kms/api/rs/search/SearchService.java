package nl.wunderbieb.kms.api.rs.search;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import nl.wunderbieb.kms.api.rs.search.dto.SearchDocumentResult;
import nl.wunderbieb.kms.api.rs.search.dto.SearchDocumentTypeResult;
import nl.wunderbieb.kms.api.rs.search.dto.SearchResponse;
import nl.wunderbieb.kms.api.rs.search.dto.SearchTopicResult;
import nl.wunderbieb.kms.docs.domain.DocumentOnboardingStatus;
import nl.wunderbieb.kms.docs.domain.DocumentWorkflowStatus;
import nl.wunderbieb.kms.docs.repository.DocumentEntity;
import nl.wunderbieb.kms.docs.repository.DocumentRepository;
import nl.wunderbieb.kms.docs.repository.DocumentUserStatusEntity;
import nl.wunderbieb.kms.docs.repository.DocumentUserStatusRepository;
import nl.wunderbieb.kms.taxonomy.repository.DocumentTypeDefinitionEntity;
import nl.wunderbieb.kms.taxonomy.repository.DocumentTypeDefinitionRepository;
import nl.wunderbieb.kms.taxonomy.repository.InspectionDomainEntity;
import nl.wunderbieb.kms.taxonomy.repository.InspectionDomainRepository;
import nl.wunderbieb.kms.taxonomy.repository.InspectionTopicEntity;
import nl.wunderbieb.kms.taxonomy.repository.InspectionTopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SearchService {

  private final DocumentRepository documentRepository;
  private final DocumentUserStatusRepository documentUserStatusRepository;
  private final InspectionTopicRepository inspectionTopicRepository;
  private final InspectionDomainRepository inspectionDomainRepository;
  private final DocumentTypeDefinitionRepository documentTypeDefinitionRepository;

  public SearchService(
      DocumentRepository documentRepository,
      DocumentUserStatusRepository documentUserStatusRepository,
      InspectionTopicRepository inspectionTopicRepository,
      InspectionDomainRepository inspectionDomainRepository,
      DocumentTypeDefinitionRepository documentTypeDefinitionRepository
  ) {
    this.documentRepository = documentRepository;
    this.documentUserStatusRepository = documentUserStatusRepository;
    this.inspectionTopicRepository = inspectionTopicRepository;
    this.inspectionDomainRepository = inspectionDomainRepository;
    this.documentTypeDefinitionRepository = documentTypeDefinitionRepository;
  }

  public SearchResponse search(
      long actorUserId,
      String query,
      String documentTypeCode,
      String workflowStatusRaw,
      Long schoolId,
      String domainCode
  ) {
    String normalizedQuery = normalize(query);
    String normalizedDocumentTypeCode = normalize(documentTypeCode);
    String normalizedDomainCode = normalize(domainCode);
    DocumentWorkflowStatus workflowStatus = parseWorkflowStatus(workflowStatusRaw);
    String termLike = normalizedQuery != null ? "%" + normalizedQuery.toLowerCase(Locale.ROOT) + "%" : null;

    List<DocumentEntity> documents = documentRepository.searchDocuments(
        termLike,
        normalizedDocumentTypeCode,
        workflowStatus,
        schoolId
    );

    Map<Long, DocumentUserStatusEntity> userStatusByDocumentId = resolveUserStatusByDocumentId(documents, actorUserId);
    List<SearchDocumentResult> documentResults = documents.stream()
        .map(document -> toDocumentResult(document, userStatusByDocumentId.get(document.getId())))
        .toList();

    Map<Long, InspectionDomainEntity> domainsById = inspectionDomainRepository.findAllByOrderBySortOrderAscIdAsc().stream()
        .collect(Collectors.toMap(InspectionDomainEntity::getId, Function.identity()));
    Optional<Long> filterDomainId = resolveDomainId(normalizedDomainCode, domainsById.values().stream().toList());
    List<InspectionTopicEntity> topics = filterDomainId
        .map(domainId -> inspectionTopicRepository.findAllByDomainIdInOrderByDomainIdAscSortOrderAscIdAsc(List.of(domainId)))
        .orElseGet(inspectionTopicRepository::findAllByOrderBySortOrderAscIdAsc);
    List<SearchTopicResult> topicResults = topics.stream()
        .filter(InspectionTopicEntity::isActive)
        .map(topic -> toTopicResult(topic, domainsById.get(topic.getDomainId())))
        .filter(topic -> matchesText(normalizedQuery, topic.displayNameNl(), topic.descriptionNl(), topic.code(), topic.domainCode(), topic.domainDisplayNameNl()))
        .toList();

    List<SearchDocumentTypeResult> documentTypeResults = documentTypeDefinitionRepository.findAllByOrderByIdAsc().stream()
        .filter(DocumentTypeDefinitionEntity::isActive)
        .filter(type -> matchesText(normalizedQuery, type.getCode(), type.getDisplayNameNl(), type.getDescriptionNl()))
        .map(type -> new SearchDocumentTypeResult(
            type.getId(),
            type.getCode(),
            type.getDisplayNameNl(),
            type.getDescriptionNl(),
            type.isRequiredForOnboarding()
        ))
        .toList();

    return new SearchResponse(
        normalizedQuery,
        normalizedDocumentTypeCode,
        workflowStatus != null ? workflowStatus.name() : null,
        schoolId,
        normalizedDomainCode,
        documentResults,
        topicResults,
        documentTypeResults
    );
  }

  private Optional<Long> resolveDomainId(String domainCode, List<InspectionDomainEntity> domains) {
    if (domainCode == null) {
      return Optional.empty();
    }
    return domains.stream()
        .filter(domain -> domainCode.equalsIgnoreCase(domain.getCode()))
        .map(InspectionDomainEntity::getId)
        .findFirst();
  }

  private Map<Long, DocumentUserStatusEntity> resolveUserStatusByDocumentId(List<DocumentEntity> documents, long userId) {
    if (documents.isEmpty()) {
      return Map.of();
    }
    List<Long> documentIds = documents.stream().map(DocumentEntity::getId).toList();
    return documentUserStatusRepository.findAllByUserIdAndDocumentIdIn(userId, documentIds).stream()
        .collect(Collectors.toMap(status -> status.getDocument().getId(), Function.identity()));
  }

  private SearchDocumentResult toDocumentResult(DocumentEntity document, DocumentUserStatusEntity userStatus) {
    DocumentOnboardingStatus onboardingStatus = userStatus != null ? userStatus.getOnboardingStatus() : DocumentOnboardingStatus.OPEN;
    Instant lastReadAt = userStatus != null ? userStatus.getLastReadAt() : null;
    return new SearchDocumentResult(
        document.getId(),
        document.getTitle(),
        document.getSummary(),
        document.getDocumentTypeCode(),
        document.getWorkflowStatus().name(),
        document.getSchoolId(),
        onboardingStatus.name(),
        lastReadAt,
        document.getUpdatedAt()
    );
  }

  private SearchTopicResult toTopicResult(InspectionTopicEntity topic, InspectionDomainEntity domain) {
    String domainCode = domain != null ? domain.getCode() : null;
    String domainDisplayNameNl = domain != null ? domain.getDisplayNameNl() : null;
    return new SearchTopicResult(
        topic.getId(),
        topic.getCode(),
        topic.getDisplayNameNl(),
        topic.getDescriptionNl(),
        domainCode,
        domainDisplayNameNl
    );
  }

  private DocumentWorkflowStatus parseWorkflowStatus(String workflowStatusRaw) {
    String normalized = normalize(workflowStatusRaw);
    if (normalized == null) {
      return null;
    }
    try {
      return DocumentWorkflowStatus.valueOf(normalized.toUpperCase(Locale.ROOT));
    } catch (IllegalArgumentException exception) {
      throw new IllegalArgumentException("Onbekende workflowStatus: " + workflowStatusRaw);
    }
  }

  private boolean matchesText(String query, String... values) {
    if (query == null) {
      return true;
    }
    String needle = query.toLowerCase(Locale.ROOT);
    for (String value : values) {
      if (value != null && value.toLowerCase(Locale.ROOT).contains(needle)) {
        return true;
      }
    }
    return false;
  }

  private String normalize(String value) {
    if (value == null) {
      return null;
    }
    String trimmed = value.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }
}
