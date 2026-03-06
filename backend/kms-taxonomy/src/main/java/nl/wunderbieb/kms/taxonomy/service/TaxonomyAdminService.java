package nl.wunderbieb.kms.taxonomy.service;

import java.util.List;
import java.util.NoSuchElementException;
import nl.wunderbieb.kms.audit.service.AuditService;
import nl.wunderbieb.kms.taxonomy.domain.DocumentTypeDefinition;
import nl.wunderbieb.kms.taxonomy.domain.InspectionDomain;
import nl.wunderbieb.kms.taxonomy.domain.InspectionTopic;
import nl.wunderbieb.kms.taxonomy.repository.DocumentTypeDefinitionEntity;
import nl.wunderbieb.kms.taxonomy.repository.DocumentTypeDefinitionRepository;
import nl.wunderbieb.kms.taxonomy.repository.InspectionDomainEntity;
import nl.wunderbieb.kms.taxonomy.repository.InspectionDomainRepository;
import nl.wunderbieb.kms.taxonomy.repository.InspectionTopicEntity;
import nl.wunderbieb.kms.taxonomy.repository.InspectionTopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TaxonomyAdminService {

  private final AuditService auditService;
  private final InspectionDomainRepository inspectionDomainRepository;
  private final InspectionTopicRepository inspectionTopicRepository;
  private final DocumentTypeDefinitionRepository documentTypeDefinitionRepository;

  public TaxonomyAdminService(
      AuditService auditService,
      InspectionDomainRepository inspectionDomainRepository,
      InspectionTopicRepository inspectionTopicRepository,
      DocumentTypeDefinitionRepository documentTypeDefinitionRepository
  ) {
    this.auditService = auditService;
    this.inspectionDomainRepository = inspectionDomainRepository;
    this.inspectionTopicRepository = inspectionTopicRepository;
    this.documentTypeDefinitionRepository = documentTypeDefinitionRepository;
  }

  @Transactional(readOnly = true)
  public List<InspectionDomain> getDomains() {
    return inspectionDomainRepository.findAllByOrderBySortOrderAscIdAsc().stream()
        .map(this::toDomain)
        .toList();
  }

  @Transactional(readOnly = true)
  public List<InspectionTopic> getTopics() {
    return inspectionTopicRepository.findAllByOrderBySortOrderAscIdAsc().stream()
        .map(this::toDomain)
        .toList();
  }

  @Transactional(readOnly = true)
  public List<DocumentTypeDefinition> getDocumentTypes() {
    return documentTypeDefinitionRepository.findAllByOrderByIdAsc().stream()
        .map(this::toDomain)
        .toList();
  }

  public InspectionDomain createDomain(String actorRoleCode, String code, String displayNameNl, String descriptionNl, int sortOrder) {
    if (inspectionDomainRepository.existsByCode(code)) {
      throw new IllegalArgumentException("Inspectiedomeincode bestaat al.");
    }
    InspectionDomainEntity domain = inspectionDomainRepository.save(new InspectionDomainEntity(code, displayNameNl, descriptionNl, sortOrder, true));
    auditService.record("inspection_domain_created", actorRoleCode, "inspection_domain", domain.getCode(), "Inspectiedomein aangemaakt");
    return toDomain(domain);
  }

  public InspectionTopic createTopic(String actorRoleCode, String domainCode, String code, String displayNameNl, String descriptionNl, int sortOrder) {
    if (inspectionTopicRepository.existsByCode(code)) {
      throw new IllegalArgumentException("Inspectieonderwerpcode bestaat al.");
    }
    InspectionDomainEntity domain = inspectionDomainRepository.findByCode(domainCode)
        .orElseThrow(() -> new NoSuchElementException("Inspectiedomein niet gevonden."));
    InspectionTopicEntity topic = inspectionTopicRepository.save(new InspectionTopicEntity(
        domain.getId(),
        code,
        displayNameNl,
        descriptionNl,
        sortOrder,
        true
    ));
    auditService.record("inspection_topic_created", actorRoleCode, "inspection_topic", topic.getCode(), "Inspectieonderwerp aangemaakt");
    return toDomain(topic);
  }

  public DocumentTypeDefinition createDocumentType(
      String actorRoleCode,
      String code,
      String displayNameNl,
      String descriptionNl,
      boolean requiredForOnboarding
  ) {
    if (documentTypeDefinitionRepository.existsByCode(code)) {
      throw new IllegalArgumentException("Documenttypecode bestaat al.");
    }
    DocumentTypeDefinitionEntity documentType = documentTypeDefinitionRepository.save(new DocumentTypeDefinitionEntity(
        code,
        displayNameNl,
        descriptionNl,
        true,
        requiredForOnboarding
    ));
    auditService.record("document_type_created", actorRoleCode, "document_type", documentType.getCode(), "Documenttype aangemaakt");
    return toDomain(documentType);
  }

  private InspectionDomain toDomain(InspectionDomainEntity entity) {
    return new InspectionDomain(
        entity.getId(),
        entity.getCode(),
        entity.getDisplayNameNl(),
        entity.getDescriptionNl(),
        entity.getSortOrder(),
        entity.isActive()
    );
  }

  private InspectionTopic toDomain(InspectionTopicEntity entity) {
    return new InspectionTopic(
        entity.getId(),
        entity.getDomainId(),
        entity.getCode(),
        entity.getDisplayNameNl(),
        entity.getDescriptionNl(),
        entity.getSortOrder(),
        entity.isActive()
    );
  }

  private DocumentTypeDefinition toDomain(DocumentTypeDefinitionEntity entity) {
    return new DocumentTypeDefinition(
        entity.getId(),
        entity.getCode(),
        entity.getDisplayNameNl(),
        entity.getDescriptionNl(),
        entity.isActive(),
        entity.isRequiredForOnboarding()
    );
  }
}
