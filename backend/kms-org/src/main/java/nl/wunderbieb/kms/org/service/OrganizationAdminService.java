package nl.wunderbieb.kms.org.service;

import java.util.List;
import java.util.NoSuchElementException;
import nl.wunderbieb.kms.audit.service.AuditService;
import nl.wunderbieb.kms.org.domain.Board;
import nl.wunderbieb.kms.org.domain.BoardSchoolRelation;
import nl.wunderbieb.kms.org.domain.School;
import nl.wunderbieb.kms.org.repository.BoardEntity;
import nl.wunderbieb.kms.org.repository.BoardRepository;
import nl.wunderbieb.kms.org.repository.BoardSchoolRelationEntity;
import nl.wunderbieb.kms.org.repository.BoardSchoolRelationRepository;
import nl.wunderbieb.kms.org.repository.SchoolEntity;
import nl.wunderbieb.kms.org.repository.SchoolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrganizationAdminService {

  private final AuditService auditService;
  private final BoardRepository boardRepository;
  private final SchoolRepository schoolRepository;
  private final BoardSchoolRelationRepository boardSchoolRelationRepository;

  public OrganizationAdminService(
      AuditService auditService,
      BoardRepository boardRepository,
      SchoolRepository schoolRepository,
      BoardSchoolRelationRepository boardSchoolRelationRepository
  ) {
    this.auditService = auditService;
    this.boardRepository = boardRepository;
    this.schoolRepository = schoolRepository;
    this.boardSchoolRelationRepository = boardSchoolRelationRepository;
  }

  @Transactional(readOnly = true)
  public List<Board> getBoards() {
    return boardRepository.findAllByOrderByIdAsc().stream().map(this::toDomain).toList();
  }

  @Transactional(readOnly = true)
  public List<School> getSchools() {
    return schoolRepository.findAllByOrderByIdAsc().stream().map(this::toDomain).toList();
  }

  @Transactional(readOnly = true)
  public List<BoardSchoolRelation> getRelations() {
    return boardSchoolRelationRepository.findAllByOrderByIdAsc().stream().map(this::toDomain).toList();
  }

  public Board createBoard(String actorRoleCode, String code, String displayNameNl) {
    if (boardRepository.existsByCode(code)) {
      throw new IllegalArgumentException("Bestuurcode bestaat al.");
    }
    BoardEntity entity = boardRepository.save(new BoardEntity(code, displayNameNl, true));
    auditService.record("board_created", actorRoleCode, "board", String.valueOf(entity.getId()), "Bestuur aangemaakt");
    return toDomain(entity);
  }

  public School createSchool(String actorRoleCode, String brin, String displayNameNl) {
    if (schoolRepository.existsByBrin(brin)) {
      throw new IllegalArgumentException("School BRIN bestaat al.");
    }
    SchoolEntity entity = schoolRepository.save(new SchoolEntity(brin, displayNameNl, true));
    auditService.record("school_created", actorRoleCode, "school", String.valueOf(entity.getId()), "School aangemaakt");
    return toDomain(entity);
  }

  public School patchSchool(String actorRoleCode, long schoolId, String displayNameNl, Boolean active) {
    SchoolEntity existing = schoolRepository.findById(schoolId)
        .orElseThrow(() -> new NoSuchElementException("School niet gevonden."));
    if (displayNameNl != null && !displayNameNl.isBlank()) {
      existing.setDisplayNameNl(displayNameNl);
    }
    if (active != null) {
      existing.setActive(active);
    }
    SchoolEntity saved = schoolRepository.save(existing);
    auditService.record("school_updated", actorRoleCode, "school", String.valueOf(saved.getId()), "School bijgewerkt");
    return toDomain(saved);
  }

  public BoardSchoolRelation createRelation(String actorRoleCode, long boardId, long schoolId, String relationType) {
    if (!boardRepository.existsById(boardId)) {
      throw new NoSuchElementException("Bestuur niet gevonden.");
    }
    if (!schoolRepository.existsById(schoolId)) {
      throw new NoSuchElementException("School niet gevonden.");
    }
    if (boardSchoolRelationRepository.existsByBoardIdAndSchoolIdAndRelationType(boardId, schoolId, relationType)) {
      throw new IllegalArgumentException("Relatie bestaat al.");
    }
    BoardSchoolRelationEntity entity = boardSchoolRelationRepository.save(
        new BoardSchoolRelationEntity(boardId, schoolId, relationType, true)
    );
    auditService.record(
        "board_school_relation_created",
        actorRoleCode,
        "board_school_relation",
        String.valueOf(entity.getId()),
        "Relatie tussen bestuur en school aangemaakt"
    );
    return toDomain(entity);
  }

  private Board toDomain(BoardEntity entity) {
    return new Board(entity.getId(), entity.getCode(), entity.getDisplayNameNl(), entity.isActive());
  }

  private School toDomain(SchoolEntity entity) {
    return new School(entity.getId(), entity.getBrin(), entity.getDisplayNameNl(), entity.isActive());
  }

  private BoardSchoolRelation toDomain(BoardSchoolRelationEntity entity) {
    return new BoardSchoolRelation(entity.getId(), entity.getBoardId(), entity.getSchoolId(), entity.getRelationType(), entity.isActive());
  }
}
