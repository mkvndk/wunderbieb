package nl.wunderbieb.kms.api.rs.session;

import nl.wunderbieb.kms.api.rs.session.dto.CurrentSessionResponse;
import nl.wunderbieb.kms.api.security.AccessContext;
import nl.wunderbieb.kms.api.security.AccessContextResolver;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/session")
public class SessionController {

  private final AccessContextResolver accessContextResolver;

  public SessionController(AccessContextResolver accessContextResolver) {
    this.accessContextResolver = accessContextResolver;
  }

  @GetMapping
  public CurrentSessionResponse currentSession(@AuthenticationPrincipal Jwt jwt) {
    AccessContext accessContext = accessContextResolver.requireCurrentContext();
    return CurrentSessionResponse.from(
        accessContext,
        getLongClaim(jwt, "user_id"),
        getStringClaim(jwt, "preferred_username"),
        getDisplayName(jwt),
        getStringClaim(jwt, "email"),
        getLongClaim(jwt, "board_id"),
        getLongClaim(jwt, "school_id")
    );
  }

  private String getDisplayName(Jwt jwt) {
    String name = getStringClaim(jwt, "name");
    if (name != null && !name.isBlank()) {
      return name;
    }
    String givenName = getStringClaim(jwt, "given_name");
    String familyName = getStringClaim(jwt, "family_name");
    if (givenName == null && familyName == null) {
      return null;
    }
    return (givenName == null ? "" : givenName) + (familyName == null ? "" : " " + familyName);
  }

  private String getStringClaim(Jwt jwt, String claimName) {
    return jwt == null ? null : jwt.getClaimAsString(claimName);
  }

  private Long getLongClaim(Jwt jwt, String claimName) {
    if (jwt == null) {
      return null;
    }
    Object claim = jwt.getClaim(claimName);
    if (claim instanceof Number number) {
      return number.longValue();
    }
    if (claim instanceof String value && !value.isBlank()) {
      try {
        return Long.parseLong(value);
      } catch (NumberFormatException ignored) {
        return null;
      }
    }
    return null;
  }
}
