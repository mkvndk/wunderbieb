package nl.wunderbieb.kms.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http, KmsSecurityProperties properties) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable);
    http.authorizeHttpRequests(registry -> {
      registry.requestMatchers("/actuator/health/**", "/actuator/info").permitAll();
      if (properties.oidcEnabled()) {
        registry.requestMatchers("/api/**").authenticated();
      } else {
        registry.requestMatchers("/api/**").permitAll();
      }
      registry.anyRequest().permitAll();
    });
    if (properties.oidcEnabled()) {
      http.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
    }
    return http.build();
  }
}
