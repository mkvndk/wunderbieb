package nl.wunderbieb.kms.taxonomy.service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import nl.wunderbieb.kms.audit.service.AuditService;
import nl.wunderbieb.kms.taxonomy.domain.DocumentTypeDefinition;
import nl.wunderbieb.kms.taxonomy.domain.InspectionDomain;
import nl.wunderbieb.kms.taxonomy.domain.InspectionTopic;

public final class TaxonomyAdminService {

  private final AuditService auditService;
  private final AtomicLong domainSequence = new AtomicLong(0);
  private final AtomicLong topicSequence = new AtomicLong(0);
  private final AtomicLong documentTypeSequence = new AtomicLong(0);
  private final Map<Long, InspectionDomain> domainsById = new ConcurrentHashMap<>();
  private final Map<String, InspectionDomain> domainsByCode = new ConcurrentHashMap<>();
  private final Map<Long, InspectionTopic> topicsById = new ConcurrentHashMap<>();
  private final Map<Long, DocumentTypeDefinition> documentTypesById = new ConcurrentHashMap<>();

  public TaxonomyAdminService(AuditService auditService) {
    this.auditService = auditService;
    seedDefaults();
  }

  public List<InspectionDomain> getDomains() {
    return domainsById.values().stream()
        .sorted((left, right) -> Integer.compare(left.sortOrder(), right.sortOrder()))
        .toList();
  }

  public List<InspectionTopic> getTopics() {
    return topicsById.values().stream()
        .sorted((left, right) -> Integer.compare(left.sortOrder(), right.sortOrder()))
        .toList();
  }

  public List<DocumentTypeDefinition> getDocumentTypes() {
    return documentTypesById.values().stream()
        .sorted((left, right) -> Long.compare(left.id(), right.id()))
        .toList();
  }

  public InspectionDomain createDomain(String actorRoleCode, String code, String displayNameNl, String descriptionNl, int sortOrder) {
    if (domainsByCode.containsKey(code)) {
      throw new IllegalArgumentException("Inspectiedomeincode bestaat al.");
    }
    InspectionDomain domain = new InspectionDomain(domainSequence.incrementAndGet(), code, displayNameNl, descriptionNl, sortOrder, true);
    domainsById.put(domain.id(), domain);
    domainsByCode.put(domain.code(), domain);
    auditService.record("inspection_domain_created", actorRoleCode, "inspection_domain", domain.code(), "Inspectiedomein aangemaakt");
    return domain;
  }

  public InspectionTopic createTopic(String actorRoleCode, String domainCode, String code, String displayNameNl, String descriptionNl, int sortOrder) {
    InspectionDomain domain = domainsByCode.get(domainCode);
    if (domain == null) {
      throw new NoSuchElementException("Inspectiedomein niet gevonden.");
    }
    InspectionTopic topic = new InspectionTopic(topicSequence.incrementAndGet(), domain.id(), code, displayNameNl, descriptionNl, sortOrder, true);
    topicsById.put(topic.id(), topic);
    auditService.record("inspection_topic_created", actorRoleCode, "inspection_topic", topic.code(), "Inspectieonderwerp aangemaakt");
    return topic;
  }

  public DocumentTypeDefinition createDocumentType(
      String actorRoleCode,
      String code,
      String displayNameNl,
      String descriptionNl,
      boolean requiredForOnboarding
  ) {
    DocumentTypeDefinition documentType = new DocumentTypeDefinition(
        documentTypeSequence.incrementAndGet(),
        code,
        displayNameNl,
        descriptionNl,
        true,
        requiredForOnboarding
    );
    documentTypesById.put(documentType.id(), documentType);
    auditService.record("document_type_created", actorRoleCode, "document_type", documentType.code(), "Documenttype aangemaakt");
    return documentType;
  }

  private void seedDefaults() {
    createSeedDomain("ONZE_SCHOOL", "Onze school", "Identiteit en cultuur", 1);
    createSeedDomain("OP", "Onderwijsproces", "Onderwijsproces", 2);
    createSeedDomain("VS", "Veiligheid en schoolklimaat", "Veiligheid en schoolklimaat", 3);
    createSeedDomain("OR", "Onderwijsresultaten", "Onderwijsresultaten", 4);
    createSeedDomain("SKA", "Sturen, kwaliteitszorg en ambitie", "Sturen, kwaliteitszorg en ambitie", 5);

    createSeedDocumentType("VISION_DOCUMENT", "Visiedocument", "Documenttype voor visie en koers", true);
    createSeedDocumentType("QUALITY_CARD", "Kwaliteitskaart", "Documenttype voor uitvoeringskwaliteit", true);
    createSeedDocumentType("AMBITION_DOCUMENT", "Ambitiedocument", "Documenttype voor ambities en doelen", false);
  }

  private void createSeedDomain(String code, String displayNameNl, String descriptionNl, int sortOrder) {
    InspectionDomain domain = new InspectionDomain(domainSequence.incrementAndGet(), code, displayNameNl, descriptionNl, sortOrder, true);
    domainsById.put(domain.id(), domain);
    domainsByCode.put(domain.code(), domain);
  }

  private void createSeedDocumentType(String code, String displayNameNl, String descriptionNl, boolean requiredForOnboarding) {
    DocumentTypeDefinition documentType = new DocumentTypeDefinition(
        documentTypeSequence.incrementAndGet(),
        code,
        displayNameNl,
        descriptionNl,
        true,
        requiredForOnboarding
    );
    documentTypesById.put(documentType.id(), documentType);
  }
}
