package nl.wunderbieb.kms.docs.service;

import java.time.Instant;
import java.util.Map;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;
import nl.wunderbieb.kms.audit.service.AuditService;
import nl.wunderbieb.kms.commons.access.ScopeType;
import nl.wunderbieb.kms.docs.domain.DocumentOnboardingStatus;
import nl.wunderbieb.kms.docs.domain.DocumentSnapshot;
import nl.wunderbieb.kms.docs.domain.DocumentVersion;
import nl.wunderbieb.kms.docs.domain.DocumentVersionStatus;
import nl.wunderbieb.kms.docs.domain.DocumentWorkflowStatus;
import nl.wunderbieb.kms.docs.repository.DocumentEntity;
import nl.wunderbieb.kms.docs.repository.DocumentRepository;
import nl.wunderbieb.kms.docs.repository.DocumentUserStatusEntity;
import nl.wunderbieb.kms.docs.repository.DocumentUserStatusRepository;
import nl.wunderbieb.kms.docs.repository.DocumentVersionEntity;
import nl.wunderbieb.kms.docs.repository.DocumentVersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DocumentEditorService {

  private final AuditService auditService;
  private final DocumentRepository documentRepository;
  private final DocumentVersionRepository documentVersionRepository;
  private final DocumentUserStatusRepository documentUserStatusRepository;

  public DocumentEditorService(
      AuditService auditService,
      DocumentRepository documentRepository,
      DocumentVersionRepository documentVersionRepository,
      DocumentUserStatusRepository documentUserStatusRepository
  ) {
    this.auditService = auditService;
    this.documentRepository = documentRepository;
    this.documentVersionRepository = documentVersionRepository;
    this.documentUserStatusRepository = documentUserStatusRepository;
  }

  @Transactional(readOnly = true)
  public List<DocumentSnapshot> listDocuments(long actorUserId) {
    List<DocumentEntity> documents = documentRepository.findTop100ByOrderByUpdatedAtDesc();
    Map<Long, DocumentUserStatusEntity> userStatusByDocumentId = resolveUserStatusByDocumentId(documents, actorUserId);
    return documents.stream()
        .map(document -> toSnapshot(document, userStatusByDocumentId.get(document.getId())))
        .toList();
  }

  @Transactional(readOnly = true)
  public DocumentSnapshot getDocument(long documentId, long actorUserId) {
    DocumentEntity document = requireDocument(documentId);
    DocumentUserStatusEntity userStatus = documentUserStatusRepository.findByDocumentIdAndUserId(documentId, actorUserId).orElse(null);
    return toSnapshot(document, userStatus);
  }

  public DocumentSnapshot createDocument(
      String actorRoleCode,
      long actorUserId,
      String documentTypeCode,
      String title,
      String summary,
      String sourceReference,
      Instant publishedAt,
      ScopeType scopeType,
      Long boardId,
      Long schoolId,
      String contentJson
  ) {
    Instant now = Instant.now();
    DocumentEntity document = documentRepository.save(new DocumentEntity(
        documentTypeCode,
        title,
        summary,
        sourceReference,
        publishedAt,
        scopeType,
        boardId,
        schoolId,
        actorUserId,
        1,
        DocumentWorkflowStatus.DRAFT,
        true,
        now,
        now
    ));
    documentVersionRepository.save(new DocumentVersionEntity(
        document,
        1,
        DocumentVersionStatus.DRAFT,
        contentJson,
        "Eerste concept",
        actorUserId,
        now
    ));
    DocumentUserStatusEntity userStatus = documentUserStatusRepository.save(new DocumentUserStatusEntity(
        document,
        actorUserId,
        DocumentOnboardingStatus.OPEN,
        null,
        now,
        actorUserId
    ));
    auditService.record("document_created", actorRoleCode, "document", String.valueOf(document.getId()), "Document aangemaakt");
    return toSnapshot(document, userStatus);
  }

  public DocumentSnapshot createDraftVersion(
      String actorRoleCode,
      long actorUserId,
      long documentId,
      String contentJson,
      String changeSummary
  ) {
    DocumentEntity document = requireDocument(documentId);
    int nextVersion = document.getActiveVersionNumber() + 1;
    DocumentVersionEntity version = new DocumentVersionEntity(
        document,
        nextVersion,
        DocumentVersionStatus.DRAFT,
        contentJson,
        changeSummary,
        actorUserId,
        Instant.now()
    );
    documentVersionRepository.save(version);
    document.setActiveVersionNumber(nextVersion);
    document.setWorkflowStatus(DocumentWorkflowStatus.DRAFT);
    document.setUpdatedAt(Instant.now());
    documentRepository.save(document);
    auditService.record(
        "document_version_created",
        actorRoleCode,
        "document",
        String.valueOf(documentId),
        "Nieuwe conceptversie " + nextVersion
    );
    return toSnapshot(document, documentUserStatusRepository.findByDocumentIdAndUserId(documentId, actorUserId).orElse(null));
  }

  public DocumentSnapshot submitForReview(String actorRoleCode, long documentId, int versionNumber) {
    DocumentEntity document = requireDocument(documentId);
    DocumentVersionEntity version = requireVersion(documentId, versionNumber);
    version.setStatus(DocumentVersionStatus.IN_REVIEW);
    documentVersionRepository.save(version);
    document.setWorkflowStatus(DocumentWorkflowStatus.IN_REVIEW);
    document.setUpdatedAt(Instant.now());
    documentRepository.save(document);
    auditService.record(
        "document_submitted_for_review",
        actorRoleCode,
        "document",
        String.valueOf(documentId),
        "Versie " + versionNumber + " aangeboden ter review"
    );
    return toSnapshot(document, null);
  }

  public DocumentSnapshot approve(String actorRoleCode, long documentId, int versionNumber) {
    DocumentEntity document = requireDocument(documentId);
    DocumentVersionEntity version = requireVersion(documentId, versionNumber);
    version.setStatus(DocumentVersionStatus.APPROVED);
    documentVersionRepository.save(version);
    document.setActiveVersionNumber(versionNumber);
    document.setWorkflowStatus(DocumentWorkflowStatus.APPROVED);
    document.setUpdatedAt(Instant.now());
    documentRepository.save(document);
    auditService.record(
        "document_approved",
        actorRoleCode,
        "document",
        String.valueOf(documentId),
        "Versie " + versionNumber + " goedgekeurd"
    );
    return toSnapshot(document, null);
  }

  public DocumentSnapshot updateOnboardingStatus(
      String actorRoleCode,
      long actorUserId,
      long documentId,
      DocumentOnboardingStatus onboardingStatus
  ) {
    DocumentEntity document = requireDocument(documentId);
    Instant now = Instant.now();
    DocumentUserStatusEntity userStatus = documentUserStatusRepository.findByDocumentIdAndUserId(documentId, actorUserId)
        .orElse(new DocumentUserStatusEntity(
            document,
            actorUserId,
            DocumentOnboardingStatus.OPEN,
            null,
            now,
            actorUserId
        ));
    userStatus.setOnboardingStatus(onboardingStatus);
    if (onboardingStatus == DocumentOnboardingStatus.GELEZEN) {
      userStatus.setLastReadAt(now);
    }
    userStatus.setUpdatedAt(now);
    userStatus.setUpdatedByUserId(actorUserId);
    documentUserStatusRepository.save(userStatus);
    auditService.record(
        "document_onboarding_status_updated",
        actorRoleCode,
        "document",
        String.valueOf(documentId),
        "Onboardingstatus gezet op " + onboardingStatus.name()
    );
    return toSnapshot(document, userStatus);
  }

  private DocumentEntity requireDocument(long documentId) {
    return documentRepository.findById(documentId)
        .orElseThrow(() -> new NoSuchElementException("Document niet gevonden."));
  }

  private DocumentVersionEntity requireVersion(long documentId, int versionNumber) {
    return documentVersionRepository.findByDocumentIdAndVersionNumber(documentId, versionNumber)
        .orElseThrow(() -> new NoSuchElementException("Documentversie niet gevonden."));
  }

  private Map<Long, DocumentUserStatusEntity> resolveUserStatusByDocumentId(List<DocumentEntity> documents, long userId) {
    if (documents.isEmpty()) {
      return Map.of();
    }
    List<Long> documentIds = documents.stream().map(DocumentEntity::getId).toList();
    return documentUserStatusRepository.findAllByUserIdAndDocumentIdIn(userId, documentIds).stream()
        .collect(Collectors.toMap(status -> status.getDocument().getId(), Function.identity()));
  }

  private DocumentSnapshot toSnapshot(DocumentEntity document, DocumentUserStatusEntity userStatus) {
    List<DocumentVersion> versions = documentVersionRepository.findAllByDocumentIdOrderByVersionNumberDesc(document.getId()).stream()
        .map(this::toVersion)
        .toList();
    DocumentOnboardingStatus onboardingStatus = userStatus != null ? userStatus.getOnboardingStatus() : DocumentOnboardingStatus.OPEN;
    Instant lastReadAt = userStatus != null ? userStatus.getLastReadAt() : null;
    Instant onboardingUpdatedAt = userStatus != null ? userStatus.getUpdatedAt() : null;
    return new DocumentSnapshot(
        document.getId(),
        document.getDocumentTypeCode(),
        document.getTitle(),
        document.getSummary(),
        document.getSourceReference(),
        document.getPublishedAt(),
        document.getScopeType(),
        document.getBoardId(),
        document.getSchoolId(),
        document.getCreatedByUserId(),
        document.getActiveVersionNumber(),
        document.getWorkflowStatus(),
        document.isActive(),
        document.getCreatedAt(),
        document.getUpdatedAt(),
        onboardingStatus,
        lastReadAt,
        onboardingUpdatedAt,
        versions
    );
  }

  private DocumentVersion toVersion(DocumentVersionEntity version) {
    return new DocumentVersion(
        version.getId(),
        version.getVersionNumber(),
        version.getStatus(),
        version.getContentJson(),
        version.getChangeSummary(),
        version.getCreatedByUserId(),
        version.getCreatedAt()
    );
  }
}
