package nl.wunderbieb.kms.taxonomy.service;

import java.util.List;
import nl.wunderbieb.kms.taxonomy.model.DocumentTypeDefinition;
import nl.wunderbieb.kms.taxonomy.model.InspectionDomainDefinition;
import nl.wunderbieb.kms.taxonomy.model.InspectionTopicDefinition;
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
public class TaxonomyCatalogService {

  private final DocumentTypeDefinitionRepository documentTypeDefinitionRepository;
  private final InspectionDomainRepository inspectionDomainRepository;
  private final InspectionTopicRepository inspectionTopicRepository;

  public TaxonomyCatalogService(
      DocumentTypeDefinitionRepository documentTypeDefinitionRepository,
      InspectionDomainRepository inspectionDomainRepository,
      InspectionTopicRepository inspectionTopicRepository
  ) {
    this.documentTypeDefinitionRepository = documentTypeDefinitionRepository;
    this.inspectionDomainRepository = inspectionDomainRepository;
    this.inspectionTopicRepository = inspectionTopicRepository;
  }

  public List<DocumentTypeDefinition> findDocumentTypes() {
    return documentTypeDefinitionRepository.findAllByOrderByIdAsc().stream()
        .map(this::toDocumentTypeDefinition)
        .toList();
  }

  public List<InspectionDomainDefinition> findInspectionDomains() {
    List<InspectionDomainEntity> domains = inspectionDomainRepository.findAllByOrderBySortOrderAscIdAsc();
    List<Long> domainIds = domains.stream().map(InspectionDomainEntity::getId).toList();
    List<InspectionTopicEntity> topics = domainIds.isEmpty()
        ? List.of()
        : inspectionTopicRepository.findAllByDomainIdInOrderByDomainIdAscSortOrderAscIdAsc(domainIds);
    return domains.stream()
        .map(domain -> new InspectionDomainDefinition(
            domain.getId(),
            domain.getCode(),
            domain.getDisplayNameNl(),
            domain.getDescriptionNl(),
            domain.getSortOrder(),
            domain.isActive(),
            topics.stream()
                .filter(topic -> topic.getDomainId() == domain.getId())
                .map(this::toInspectionTopicDefinition)
                .toList()
        ))
        .toList();
  }

  private DocumentTypeDefinition toDocumentTypeDefinition(DocumentTypeDefinitionEntity entity) {
    return new DocumentTypeDefinition(
        entity.getId(),
        entity.getCode(),
        entity.getDisplayNameNl(),
        entity.getDescriptionNl(),
        entity.isActive(),
        entity.isRequiredForOnboarding()
    );
  }

  private InspectionTopicDefinition toInspectionTopicDefinition(InspectionTopicEntity entity) {
    return new InspectionTopicDefinition(
        entity.getId(),
        entity.getCode(),
        entity.getDisplayNameNl(),
        entity.getDescriptionNl(),
        entity.getSortOrder(),
        entity.isActive()
    );
  }
}
