package nl.wunderbieb.kms.users.repository;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.LinkedHashSet;
import java.util.Set;
import nl.wunderbieb.kms.commons.access.PermissionLevel;
import nl.wunderbieb.kms.commons.access.ScopeType;

@Entity
@Table(name = "roles")
public class RoleEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 100)
  private String code;

  @Column(name = "display_name_nl", nullable = false, length = 255)
  private String displayNameNl;

  @Column(name = "description_nl", nullable = false, length = 500)
  private String descriptionNl;

  @Enumerated(EnumType.STRING)
  @Column(name = "scope_type", nullable = false, length = 50)
  private ScopeType scopeType;

  @Enumerated(EnumType.STRING)
  @Column(name = "permission_level", nullable = false, length = 50)
  private PermissionLevel permissionLevel;

  @Column(name = "system_role", nullable = false)
  private boolean systemRole;

  @Column(nullable = false)
  private boolean active;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "role_capability_codes", joinColumns = @JoinColumn(name = "role_id"))
  @Column(name = "capability_code", nullable = false, length = 100)
  private Set<String> capabilityCodes = new LinkedHashSet<>();

  protected RoleEntity() {
  }

  public RoleEntity(
      String code,
      String displayNameNl,
      String descriptionNl,
      ScopeType scopeType,
      PermissionLevel permissionLevel,
      boolean systemRole,
      boolean active,
      Set<String> capabilityCodes
  ) {
    this.code = code;
    this.displayNameNl = displayNameNl;
    this.descriptionNl = descriptionNl;
    this.scopeType = scopeType;
    this.permissionLevel = permissionLevel;
    this.systemRole = systemRole;
    this.active = active;
    this.capabilityCodes = new LinkedHashSet<>(capabilityCodes);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCode() {
    return code;
  }

  public String getDisplayNameNl() {
    return displayNameNl;
  }

  public void setDisplayNameNl(String displayNameNl) {
    this.displayNameNl = displayNameNl;
  }

  public String getDescriptionNl() {
    return descriptionNl;
  }

  public void setDescriptionNl(String descriptionNl) {
    this.descriptionNl = descriptionNl;
  }

  public ScopeType getScopeType() {
    return scopeType;
  }

  public PermissionLevel getPermissionLevel() {
    return permissionLevel;
  }

  public boolean isSystemRole() {
    return systemRole;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public Set<String> getCapabilityCodes() {
    return capabilityCodes;
  }
}
