package nl.wunderbieb.kms.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import nl.wunderbieb.kms.api.config.KmsSecurityProperties;
import nl.wunderbieb.kms.commons.access.PermissionLevel;
import nl.wunderbieb.kms.commons.access.ScopeType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAccessContextRequestFilter extends OncePerRequestFilter {

  private final KmsSecurityProperties properties;

  public JwtAccessContextRequestFilter(KmsSecurityProperties properties) {
    this.properties = properties;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    if (!properties.oidcEnabled()) {
      filterChain.doFilter(request, response);
      return;
    }
    if (request.getAttribute(AccessContextRequestFilter.ACCESS_CONTEXT_ATTRIBUTE) != null) {
      filterChain.doFilter(request, response);
      return;
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
      Jwt jwt = jwtAuthenticationToken.getToken();
      request.setAttribute(AccessContextRequestFilter.ACCESS_CONTEXT_ATTRIBUTE, new AccessContext(
          resolveRoleCode(jwt),
          resolveScopeType(jwt),
          resolvePermissionLevel(jwt),
          resolveCapabilities(jwt)
      ));
    }
    filterChain.doFilter(request, response);
  }

  private String resolveRoleCode(Jwt jwt) {
    Object roleCodes = jwt.getClaims().get("role_codes");
    if (roleCodes instanceof Collection<?> collection && !collection.isEmpty()) {
      return String.valueOf(collection.iterator().next());
    }
    return jwt.getClaimAsString("role_code") != null ? jwt.getClaimAsString("role_code") : "AUTHENTICATED_USER";
  }

  private ScopeType resolveScopeType(Jwt jwt) {
    String value = jwt.getClaimAsString("scope_type");
    if (value == null || value.isBlank()) {
      return ScopeType.SCHOOL;
    }
    return ScopeType.valueOf(value.toUpperCase());
  }

  private PermissionLevel resolvePermissionLevel(Jwt jwt) {
    String value = jwt.getClaimAsString("permission_level");
    if (value == null || value.isBlank()) {
      return PermissionLevel.READ;
    }
    return PermissionLevel.valueOf(value.toUpperCase());
  }

  private Set<String> resolveCapabilities(Jwt jwt) {
    List<String> capabilities = jwt.getClaimAsStringList("capabilities");
    return capabilities == null ? Set.of() : Set.copyOf(capabilities);
  }
}
