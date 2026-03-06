package nl.wunderbieb.kms.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import nl.wunderbieb.kms.users.domain.Role;
import nl.wunderbieb.kms.users.service.RoleAdminService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class AccessContextRequestFilter extends OncePerRequestFilter {

  static final String ACCESS_CONTEXT_ATTRIBUTE = AccessContextRequestFilter.class.getName() + ".accessContext";
  private static final String ROLE_HEADER = "X-Demo-Role-Code";

  private final RoleAdminService roleAdminService;

  public AccessContextRequestFilter(RoleAdminService roleAdminService) {
    this.roleAdminService = roleAdminService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String roleCode = request.getHeader(ROLE_HEADER);
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
