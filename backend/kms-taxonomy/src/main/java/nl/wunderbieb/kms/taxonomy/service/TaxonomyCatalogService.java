package nl.wunderbieb.kms.taxonomy.service;

import java.util.List;
import nl.wunderbieb.kms.taxonomy.model.DocumentTypeDefinition;
import nl.wunderbieb.kms.taxonomy.model.InspectionDomainDefinition;
import nl.wunderbieb.kms.taxonomy.model.InspectionTopicDefinition;
import org.springframework.stereotype.Service;

@Service
public class TaxonomyCatalogService {

  private static final List<DocumentTypeDefinition> DOCUMENT_TYPES = List.of(
      new DocumentTypeDefinition(1L, "VISION_DOCUMENT", "Visiedocument", "Documenttype voor visie en koers", true, true),
      new DocumentTypeDefinition(2L, "QUALITY_CARD", "Kwaliteitskaart", "Documenttype voor werkwijzen en kwaliteitsborging", true, true),
      new DocumentTypeDefinition(3L, "AMBITION_DOCUMENT", "Ambitiedocument", "Documenttype voor ontwikkeldoelen", true, false)
  );

  private static final List<InspectionDomainDefinition> DOMAINS = List.of(
      new InspectionDomainDefinition(
          1L,
          "SKA",
          "Sturen, kwaliteitszorg en ambitie",
          "Hoofddomein voor kwaliteitssturing",
          1,
          true,
          List.of(
              new InspectionTopicDefinition(1L, "SKA1", "Visie, ambities en doelen", "Strategische koers en doelstelling", 1, true),
              new InspectionTopicDefinition(2L, "SKA2", "Uitvoering en kwaliteitscultuur", "Dagelijkse uitvoering en cultuur", 2, true)
          )
      ),
      new InspectionDomainDefinition(
          2L,
          "OP",
          "Onderwijsproces",
          "Hoofddomein voor onderwijsproces en begeleiding",
          2,
          true,
          List.of(
              new InspectionTopicDefinition(3L, "OP1", "Aanbod", "Onderwijsaanbod en dekking", 1, true),
              new InspectionTopicDefinition(4L, "OP2", "Zicht op ontwikkeling", "Signalering en begeleiding", 2, true)
          )
      )
  );

  public List<DocumentTypeDefinition> findDocumentTypes() {
    return DOCUMENT_TYPES;
  }

  public List<InspectionDomainDefinition> findInspectionDomains() {
    return DOMAINS;
  }
}
