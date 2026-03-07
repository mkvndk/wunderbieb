package nl.wunderbieb.kms.api.config;

import nl.wunderbieb.kms.api.security.AccessContextRequestFilter;
import nl.wunderbieb.kms.api.security.JwtAccessContextRequestFilter;
import nl.wunderbieb.kms.api.security.OidcAccessContextService;
import nl.wunderbieb.kms.users.service.RoleAdminService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

  @Bean
  AccessContextRequestFilter accessContextRequestFilter(RoleAdminService roleAdminService, KmsSecurityProperties properties) {
    return new AccessContextRequestFilter(roleAdminService, properties);
  }

  @Bean
  JwtAccessContextRequestFilter jwtAccessContextRequestFilter(
      KmsSecurityProperties properties,
      OidcAccessContextService oidcAccessContextService
  ) {
    return new JwtAccessContextRequestFilter(properties, oidcAccessContextService);
  }

  @Bean
  JwtDecoder jwtDecoder(KmsSecurityProperties properties) {
    if (!properties.oidcEnabled()) {
      return unusedToken -> {
        throw new IllegalStateException("JwtDecoder mag niet gebruikt worden als OIDC uit staat.");
      };
    }
    if (properties.oidcJwkSetUri() != null && !properties.oidcJwkSetUri().isBlank()) {
      return NimbusJwtDecoder.withJwkSetUri(properties.oidcJwkSetUri()).build();
    }
    if (properties.oidcIssuerUri() != null && !properties.oidcIssuerUri().isBlank()) {
      return JwtDecoders.fromIssuerLocation(properties.oidcIssuerUri());
    }
    throw new IllegalStateException("OIDC staat aan, maar oidcIssuerUri of oidcJwkSetUri ontbreekt.");
  }

  @Bean
  SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      KmsSecurityProperties properties,
      AccessContextRequestFilter accessContextRequestFilter,
      JwtAccessContextRequestFilter jwtAccessContextRequestFilter
  ) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable);
    http.cors(Customizer.withDefaults());
    http.authorizeHttpRequests(registry -> {
      registry.requestMatchers("/actuator/health/**", "/actuator/info").permitAll();
      if (properties.oidcEnabled()) {
        registry.requestMatchers("/api/**").authenticated();
      } else {
        registry.requestMatchers("/api/**").permitAll();
      }
      registry.anyRequest().permitAll();
    });
    http.addFilterBefore(accessContextRequestFilter, AuthorizationFilter.class);
    if (properties.oidcEnabled()) {
      http.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
      http.addFilterAfter(jwtAccessContextRequestFilter, BearerTokenAuthenticationFilter.class);
    }
    return http.build();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(java.util.List.of("http://localhost:15173", "http://localhost:5173"));
    configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PATCH", "OPTIONS"));
    configuration.setAllowedHeaders(java.util.List.of("Authorization", "Content-Type", "X-Requested-With"));
    configuration.setAllowCredentials(false);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
