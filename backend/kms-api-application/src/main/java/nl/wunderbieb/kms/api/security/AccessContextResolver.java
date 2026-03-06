package nl.wunderbieb.kms.api.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class AccessContextResolver {

  private final HttpServletRequest request;

  public AccessContextResolver(HttpServletRequest request) {
    this.request = request;
  }

  public AccessContext requireCurrentContext() {
    Object value = request.getAttribute(AccessContextRequestFilter.ACCESS_CONTEXT_ATTRIBUTE);
    if (value instanceof AccessContext accessContext) {
      return accessContext;
    }
    throw new UnauthorizedException();
  }
}
