package nl.wunderbieb.kms.users.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import nl.wunderbieb.kms.commons.access.PermissionLevel;
import nl.wunderbieb.kms.commons.access.ScopeType;

@Entity
@Table(name = "user_assignments")
public class UserAssignmentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private long userId;

  @Column(name = "role_code", nullable = false, length = 100)
  private String roleCode;

  @Enumerated(EnumType.STRING)
  @Column(name = "scope_type", nullable = false, length = 50)
  private ScopeType scopeType;

  @Column(name = "board_id")
  private Long boardId;

  @Column(name = "school_id")
  private Long schoolId;

  @Enumerated(EnumType.STRING)
  @Column(name = "permission_level", nullable = false, length = 50)
  private PermissionLevel permissionLevel;

  @Column(nullable = false)
  private boolean active;

  @Column(name = "valid_from")
  private Instant validFrom;

  @Column(name = "valid_to")
  private Instant validTo;

  protected UserAssignmentEntity() {
  }

  public UserAssignmentEntity(
      long userId,
      String roleCode,
      ScopeType scopeType,
      Long boardId,
      Long schoolId,
      PermissionLevel permissionLevel,
      boolean active,
      Instant validFrom,
      Instant validTo
  ) {
    this.userId = userId;
    this.roleCode = roleCode;
    this.scopeType = scopeType;
    this.boardId = boardId;
    this.schoolId = schoolId;
    this.permissionLevel = permissionLevel;
    this.active = active;
    this.validFrom = validFrom;
    this.validTo = validTo;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public long getUserId() {
    return userId;
  }

  public String getRoleCode() {
    return roleCode;
  }

  public void setRoleCode(String roleCode) {
    this.roleCode = roleCode;
  }

  public ScopeType getScopeType() {
    return scopeType;
  }

  public Long getBoardId() {
    return boardId;
  }

  public Long getSchoolId() {
    return schoolId;
  }

  public PermissionLevel getPermissionLevel() {
    return permissionLevel;
  }

  public void setPermissionLevel(PermissionLevel permissionLevel) {
    this.permissionLevel = permissionLevel;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public Instant getValidFrom() {
    return validFrom;
  }

  public void setValidFrom(Instant validFrom) {
    this.validFrom = validFrom;
  }

  public Instant getValidTo() {
    return validTo;
  }

  public void setValidTo(Instant validTo) {
    this.validTo = validTo;
  }
}
