package nl.wunderbieb.kms.docs.service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import nl.wunderbieb.kms.audit.service.AuditService;
import nl.wunderbieb.kms.commons.access.ScopeType;
import nl.wunderbieb.kms.docs.domain.DocumentSnapshot;
import nl.wunderbieb.kms.docs.domain.DocumentVersion;
import nl.wunderbieb.kms.docs.domain.DocumentVersionStatus;
import nl.wunderbieb.kms.docs.domain.DocumentWorkflowStatus;
import nl.wunderbieb.kms.docs.repository.DocumentEntity;
import nl.wunderbieb.kms.docs.repository.DocumentRepository;
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

  public DocumentEditorService(
      AuditService auditService,
      DocumentRepository documentRepository,
      DocumentVersionRepository documentVersionRepository
  ) {
    this.auditService = auditService;
    this.documentRepository = documentRepository;
    this.documentVersionRepository = documentVersionRepository;
  }

  @Transactional(readOnly = true)
  public List<DocumentSnapshot> listDocuments() {
    return documentRepository.findTop100ByOrderByUpdatedAtDesc().stream()
        .map(this::toSnapshot)
        .toList();
  }

  @Transactional(readOnly = true)
  public DocumentSnapshot getDocument(long documentId) {
    return toSnapshot(requireDocument(documentId));
  }

  public DocumentSnapshot createDocument(
      String actorRoleCode,
      long actorUserId,
      ScopeType scopeType,
      Long boardId,
      Long schoolId,
      String documentTypeCode,
      String title,
      String contentJson
  ) {
    Instant now = Instant.now();
    DocumentEntity document = documentRepository.save(new DocumentEntity(
        documentTypeCode,
        title,
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
    auditService.record("document_created", actorRoleCode, "document", String.valueOf(document.getId()), "Document aangemaakt");
    return toSnapshot(document);
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
    return toSnapshot(document);
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
    return toSnapshot(document);
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
    return toSnapshot(document);
  }

  private DocumentEntity requireDocument(long documentId) {
    return documentRepository.findById(documentId)
        .orElseThrow(() -> new NoSuchElementException("Document niet gevonden."));
  }

  private DocumentVersionEntity requireVersion(long documentId, int versionNumber) {
    return documentVersionRepository.findByDocumentIdAndVersionNumber(documentId, versionNumber)
        .orElseThrow(() -> new NoSuchElementException("Documentversie niet gevonden."));
  }

  private DocumentSnapshot toSnapshot(DocumentEntity document) {
    List<DocumentVersion> versions = documentVersionRepository.findAllByDocumentIdOrderByVersionNumberDesc(document.getId()).stream()
        .map(this::toVersion)
        .toList();
    return new DocumentSnapshot(
        document.getId(),
        document.getDocumentTypeCode(),
        document.getTitle(),
        document.getScopeType(),
        document.getBoardId(),
        document.getSchoolId(),
        document.getCreatedByUserId(),
        document.getActiveVersionNumber(),
        document.getWorkflowStatus(),
        document.isActive(),
        document.getCreatedAt(),
        document.getUpdatedAt(),
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
