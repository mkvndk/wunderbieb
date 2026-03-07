package nl.wunderbieb.kms.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import nl.wunderbieb.kms.api.config.KmsSecurityProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAccessContextRequestFilter extends OncePerRequestFilter {

  private final KmsSecurityProperties properties;
  private final OidcAccessContextService oidcAccessContextService;

  public JwtAccessContextRequestFilter(KmsSecurityProperties properties, OidcAccessContextService oidcAccessContextService) {
    this.properties = properties;
    this.oidcAccessContextService = oidcAccessContextService;
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
      oidcAccessContextService.resolve(jwt)
          .ifPresent(accessContext -> request.setAttribute(AccessContextRequestFilter.ACCESS_CONTEXT_ATTRIBUTE, accessContext));
    }
    filterChain.doFilter(request, response);
  }
}
