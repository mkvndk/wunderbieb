package nl.wunderbieb.kms.api.rs.admin;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import nl.wunderbieb.kms.api.rs.admin.dto.DocumentTypeCreateRequest;
import nl.wunderbieb.kms.api.rs.admin.dto.InspectionDomainCreateRequest;
import nl.wunderbieb.kms.api.rs.admin.dto.InspectionTopicCreateRequest;
import nl.wunderbieb.kms.api.security.AccessContextResolver;
import nl.wunderbieb.kms.taxonomy.domain.DocumentTypeDefinition;
import nl.wunderbieb.kms.taxonomy.domain.InspectionDomain;
import nl.wunderbieb.kms.taxonomy.domain.InspectionTopic;
import nl.wunderbieb.kms.taxonomy.service.TaxonomyAdminService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminTaxonomyController {

  private final TaxonomyAdminService taxonomyAdminService;
  private final AccessContextResolver accessContextResolver;

  public AdminTaxonomyController(TaxonomyAdminService taxonomyAdminService, AccessContextResolver accessContextResolver) {
    this.taxonomyAdminService = taxonomyAdminService;
    this.accessContextResolver = accessContextResolver;
  }

  @GetMapping("/inspection-domains")
  public Map<String, List<?>> listDomains() {
    accessContextResolver.requireCurrentContext().requireCapability("MANAGE_TAXONOMY");
    return Map.of(
        "domains", taxonomyAdminService.getDomains(),
        "topics", taxonomyAdminService.getTopics()
    );
  }

  @PostMapping("/inspection-domains")
  public InspectionDomain createDomain(@Valid @RequestBody InspectionDomainCreateRequest request) {
    String actorRoleCode = accessContextResolver.requireCurrentContext().roleCode();
    accessContextResolver.requireCurrentContext().requireCapability("MANAGE_TAXONOMY");
    return taxonomyAdminService.createDomain(actorRoleCode, request.code(), request.displayNameNl(), request.descriptionNl(), request.sortOrder());
  }

  @PostMapping("/inspection-topics")
  public InspectionTopic createTopic(@Valid @RequestBody InspectionTopicCreateRequest request) {
    String actorRoleCode = accessContextResolver.requireCurrentContext().roleCode();
    accessContextResolver.requireCurrentContext().requireCapability("MANAGE_TAXONOMY");
    return taxonomyAdminService.createTopic(
        actorRoleCode,
        request.domainCode(),
        request.code(),
        request.displayNameNl(),
        request.descriptionNl(),
        request.sortOrder()
    );
  }

  @GetMapping("/document-types")
  public List<DocumentTypeDefinition> listDocumentTypes() {
    accessContextResolver.requireCurrentContext().requireCapability("MANAGE_TAXONOMY");
    return taxonomyAdminService.getDocumentTypes();
  }

  @PostMapping("/document-types")
  public DocumentTypeDefinition createDocumentType(@Valid @RequestBody DocumentTypeCreateRequest request) {
    String actorRoleCode = accessContextResolver.requireCurrentContext().roleCode();
    accessContextResolver.requireCurrentContext().requireCapability("MANAGE_TAXONOMY");
    return taxonomyAdminService.createDocumentType(
        actorRoleCode,
        request.code(),
        request.displayNameNl(),
        request.descriptionNl(),
        request.requiredForOnboarding()
    );
  }
}
