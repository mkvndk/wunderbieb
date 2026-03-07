package nl.wunderbieb.kms.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import nl.wunderbieb.kms.api.config.KmsSecurityProperties;
import nl.wunderbieb.kms.users.domain.Role;
import nl.wunderbieb.kms.users.service.RoleAdminService;
import org.springframework.web.filter.OncePerRequestFilter;

public class AccessContextRequestFilter extends OncePerRequestFilter {

  static final String ACCESS_CONTEXT_ATTRIBUTE = AccessContextRequestFilter.class.getName() + ".accessContext";
  private final RoleAdminService roleAdminService;
  private final KmsSecurityProperties properties;

  public AccessContextRequestFilter(RoleAdminService roleAdminService, KmsSecurityProperties properties) {
    this.roleAdminService = roleAdminService;
    this.properties = properties;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    if (!properties.debugRoleHeaderEnabled()) {
      filterChain.doFilter(request, response);
      return;
    }
    String roleCode = request.getHeader(properties.debugRoleHeaderName());
    if (roleCode != null && !roleCode.isBlank()) {
      Role role = roleAdminService.requireRoleByCode(roleCode);
      request.setAttribute(ACCESS_CONTEXT_ATTRIBUTE, new AccessContext(
          role.code(),
          role.scopeType(),
          role.permissionLevel(),
          role.capabilityCodes()
      ));
    }
    filterChain.doFilter(request, response);
  }
}
