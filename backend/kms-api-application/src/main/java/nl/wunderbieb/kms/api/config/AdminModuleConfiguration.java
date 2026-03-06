package nl.wunderbieb.kms.api.config;

import nl.wunderbieb.kms.audit.service.AuditService;
import nl.wunderbieb.kms.audit.service.InMemoryAuditService;
import nl.wunderbieb.kms.insights.service.ScoreConfigurationAdminService;
import nl.wunderbieb.kms.taxonomy.service.TaxonomyAdminService;
import nl.wunderbieb.kms.users.service.RoleAdminService;
import nl.wunderbieb.kms.users.service.UserAssignmentAdminService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminModuleConfiguration {

  @Bean
  AuditService auditService() {
    return new InMemoryAuditService();
  }

  @Bean
  RoleAdminService roleAdminService(AuditService auditService) {
    return new RoleAdminService(auditService);
  }

  @Bean
  UserAssignmentAdminService userAssignmentAdminService(RoleAdminService roleAdminService, AuditService auditService) {
    return new UserAssignmentAdminService(roleAdminService, auditService);
  }

  @Bean
  TaxonomyAdminService taxonomyAdminService(AuditService auditService) {
    return new TaxonomyAdminService(auditService);
  }

  @Bean
  ScoreConfigurationAdminService scoreConfigurationAdminService(AuditService auditService) {
    return new ScoreConfigurationAdminService(auditService);
  }
}
