package nl.wunderbieb.kms.api.rs.search;

import nl.wunderbieb.kms.api.rs.search.dto.SearchResponse;
import nl.wunderbieb.kms.api.security.AccessContextResolver;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
public class SearchController {

  private final AccessContextResolver accessContextResolver;
  private final SearchService searchService;

  public SearchController(AccessContextResolver accessContextResolver, SearchService searchService) {
    this.accessContextResolver = accessContextResolver;
    this.searchService = searchService;
  }

  @GetMapping
  public SearchResponse search(
      @RequestParam(required = false) String query,
      @RequestParam(required = false) String documentTypeCode,
      @RequestParam(required = false) String workflowStatus,
      @RequestParam(required = false) Long schoolId,
      @RequestParam(required = false) String domainCode,
      @AuthenticationPrincipal Jwt jwt
  ) {
    accessContextResolver.requireCurrentContext();
    return searchService.search(
        getRequiredUserId(jwt),
        query,
        documentTypeCode,
        workflowStatus,
        schoolId,
        domainCode
    );
  }

  private long getRequiredUserId(Jwt jwt) {
    if (jwt == null) {
      throw new IllegalArgumentException("Claim user_id ontbreekt.");
    }
    Object claim = jwt.getClaim("user_id");
    if (claim instanceof Number number) {
      return number.longValue();
    }
    if (claim instanceof String value && !value.isBlank()) {
      try {
        return Long.parseLong(value);
      } catch (NumberFormatException ignored) {
        throw new IllegalArgumentException("Claim user_id ontbreekt.");
      }
    }
    throw new IllegalArgumentException("Claim user_id ontbreekt.");
  }
}
