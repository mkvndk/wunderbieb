package nl.wunderbieb.kms.api.rs.docs;

import jakarta.validation.Valid;
import java.util.List;
import nl.wunderbieb.kms.api.rs.docs.dto.DocumentCreateRequest;
import nl.wunderbieb.kms.api.rs.docs.dto.DocumentOnboardingStatusRequest;
import nl.wunderbieb.kms.api.rs.docs.dto.DocumentResponse;
import nl.wunderbieb.kms.api.rs.docs.dto.DocumentVersionCreateRequest;
import nl.wunderbieb.kms.api.rs.docs.dto.DocumentWorkflowRequest;
import nl.wunderbieb.kms.api.security.AccessContext;
import nl.wunderbieb.kms.api.security.AccessContextResolver;
import nl.wunderbieb.kms.docs.domain.DocumentSnapshot;
import nl.wunderbieb.kms.docs.service.DocumentEditorService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/docs")
public class DocumentController {

  private final AccessContextResolver accessContextResolver;
  private final DocumentEditorService documentEditorService;

  public DocumentController(AccessContextResolver accessContextResolver, DocumentEditorService documentEditorService) {
    this.accessContextResolver = accessContextResolver;
    this.documentEditorService = documentEditorService;
  }

  @GetMapping("/documents")
  public List<DocumentResponse> listDocuments(@AuthenticationPrincipal Jwt jwt) {
    AccessContext context = accessContextResolver.requireCurrentContext();
    context.requireCapability("EDIT_DOCUMENT");
    return documentEditorService.listDocuments(getRequiredUserId(jwt)).stream().map(DocumentResponse::from).toList();
  }

  @GetMapping("/documents/{documentId}")
  public DocumentResponse getDocument(@PathVariable long documentId, @AuthenticationPrincipal Jwt jwt) {
    AccessContext context = accessContextResolver.requireCurrentContext();
    context.requireCapability("EDIT_DOCUMENT");
    return DocumentResponse.from(documentEditorService.getDocument(documentId, getRequiredUserId(jwt)));
  }

  @PostMapping("/documents")
  public DocumentResponse createDocument(
      @Valid @RequestBody DocumentCreateRequest request,
      @AuthenticationPrincipal Jwt jwt
  ) {
    AccessContext context = accessContextResolver.requireCurrentContext();
    context.requireCapability("EDIT_DOCUMENT");
    DocumentSnapshot snapshot = documentEditorService.createDocument(
        context.roleCode(),
        getRequiredUserId(jwt),
        request.documentTypeCode(),
        request.title(),
        request.summary(),
        request.sourceReference(),
        request.publishedAt(),
        context.scopeType(),
        getLongClaim(jwt, "board_id"),
        getLongClaim(jwt, "school_id"),
        request.contentJson()
    );
    return DocumentResponse.from(snapshot);
  }

  @PostMapping("/documents/{documentId}/versions")
  public DocumentResponse createVersion(
      @PathVariable long documentId,
      @Valid @RequestBody DocumentVersionCreateRequest request,
      @AuthenticationPrincipal Jwt jwt
  ) {
    AccessContext context = accessContextResolver.requireCurrentContext();
    context.requireCapability("EDIT_DOCUMENT");
    return DocumentResponse.from(documentEditorService.createDraftVersion(
        context.roleCode(),
        getRequiredUserId(jwt),
        documentId,
        request.contentJson(),
        request.changeSummary() != null && !request.changeSummary().isBlank() ? request.changeSummary() : "Concept bewerkt"
    ));
  }

  @PostMapping("/documents/{documentId}/review")
  public DocumentResponse submitForReview(
      @PathVariable long documentId,
      @Valid @RequestBody DocumentWorkflowRequest request
  ) {
    AccessContext context = accessContextResolver.requireCurrentContext();
    context.requireCapability("REVIEW_DOCUMENT");
    return DocumentResponse.from(documentEditorService.submitForReview(context.roleCode(), documentId, request.versionNumber()));
  }

  @PostMapping("/documents/{documentId}/approve")
  public DocumentResponse approve(
      @PathVariable long documentId,
      @Valid @RequestBody DocumentWorkflowRequest request
  ) {
    AccessContext context = accessContextResolver.requireCurrentContext();
    context.requireCapability("APPROVE_DOCUMENT");
    return DocumentResponse.from(documentEditorService.approve(context.roleCode(), documentId, request.versionNumber()));
  }

  @PatchMapping("/documents/{documentId}/onboarding-status")
  public DocumentResponse updateOnboardingStatus(
      @PathVariable long documentId,
      @Valid @RequestBody DocumentOnboardingStatusRequest request,
      @AuthenticationPrincipal Jwt jwt
  ) {
    AccessContext context = accessContextResolver.requireCurrentContext();
    context.requireCapability("EDIT_DOCUMENT");
    return DocumentResponse.from(documentEditorService.updateOnboardingStatus(
        context.roleCode(),
        getRequiredUserId(jwt),
        documentId,
        request.status()
    ));
  }

  private long getRequiredUserId(Jwt jwt) {
    Long userId = getLongClaim(jwt, "user_id");
    if (userId == null) {
      throw new IllegalArgumentException("Claim user_id ontbreekt.");
    }
    return userId;
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
