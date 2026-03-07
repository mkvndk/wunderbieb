package nl.wunderbieb.kms.api.rs.admin;

import jakarta.validation.Valid;
import java.util.List;
import nl.wunderbieb.kms.api.rs.admin.dto.BoardCreateRequest;
import nl.wunderbieb.kms.api.rs.admin.dto.BoardSchoolRelationCreateRequest;
import nl.wunderbieb.kms.api.rs.admin.dto.SchoolCreateRequest;
import nl.wunderbieb.kms.api.rs.admin.dto.SchoolPatchRequest;
import nl.wunderbieb.kms.api.security.AccessContextResolver;
import nl.wunderbieb.kms.org.domain.Board;
import nl.wunderbieb.kms.org.domain.BoardSchoolRelation;
import nl.wunderbieb.kms.org.domain.School;
import nl.wunderbieb.kms.org.service.OrganizationAdminService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminOrganizationController {

  private final OrganizationAdminService organizationAdminService;
  private final AccessContextResolver accessContextResolver;

  public AdminOrganizationController(
      OrganizationAdminService organizationAdminService,
      AccessContextResolver accessContextResolver
  ) {
    this.organizationAdminService = organizationAdminService;
    this.accessContextResolver = accessContextResolver;
  }

  @GetMapping("/boards")
  public List<Board> listBoards() {
    accessContextResolver.requireCurrentContext().requireCapability("MANAGE_ORG");
    return organizationAdminService.getBoards();
  }

  @PostMapping("/boards")
  public Board createBoard(@Valid @RequestBody BoardCreateRequest request) {
    String actorRoleCode = accessContextResolver.requireCurrentContext().roleCode();
    accessContextResolver.requireCurrentContext().requireCapability("MANAGE_ORG");
    return organizationAdminService.createBoard(actorRoleCode, request.code(), request.displayNameNl());
  }

  @GetMapping("/schools")
  public List<School> listSchools() {
    accessContextResolver.requireCurrentContext().requireCapability("MANAGE_ORG");
    return organizationAdminService.getSchools();
  }

  @PostMapping("/schools")
  public School createSchool(@Valid @RequestBody SchoolCreateRequest request) {
    String actorRoleCode = accessContextResolver.requireCurrentContext().roleCode();
    accessContextResolver.requireCurrentContext().requireCapability("MANAGE_ORG");
    return organizationAdminService.createSchool(actorRoleCode, request.brin(), request.displayNameNl());
  }

  @PatchMapping("/schools/{schoolId}")
  public School patchSchool(@PathVariable long schoolId, @RequestBody SchoolPatchRequest request) {
    String actorRoleCode = accessContextResolver.requireCurrentContext().roleCode();
    accessContextResolver.requireCurrentContext().requireCapability("MANAGE_ORG");
    return organizationAdminService.patchSchool(actorRoleCode, schoolId, request.displayNameNl(), request.active());
  }

  @GetMapping("/board-school-relations")
  public List<BoardSchoolRelation> listBoardSchoolRelations() {
    accessContextResolver.requireCurrentContext().requireCapability("MANAGE_ORG");
    return organizationAdminService.getRelations();
  }

  @PostMapping("/board-school-relations")
  public BoardSchoolRelation createBoardSchoolRelation(@Valid @RequestBody BoardSchoolRelationCreateRequest request) {
    String actorRoleCode = accessContextResolver.requireCurrentContext().roleCode();
    accessContextResolver.requireCurrentContext().requireCapability("MANAGE_ORG");
    return organizationAdminService.createRelation(
        actorRoleCode,
        request.boardId(),
        request.schoolId(),
        request.relationType()
    );
  }
}
