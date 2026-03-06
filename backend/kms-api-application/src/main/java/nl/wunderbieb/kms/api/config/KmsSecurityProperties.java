package nl.wunderbieb.kms.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kms.security")
public record KmsSecurityProperties(
    boolean oidcEnabled,
    boolean debugRoleHeaderEnabled,
    String debugRoleHeaderName,
    String adminRequiredRoleCode
) {
}
