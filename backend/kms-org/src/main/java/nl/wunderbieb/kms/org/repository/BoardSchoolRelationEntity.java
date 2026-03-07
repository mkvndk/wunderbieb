package nl.wunderbieb.kms.org.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "board_school_relations",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_board_school_relation", columnNames = {"board_id", "school_id", "relation_type"})
    }
)
public class BoardSchoolRelationEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "board_id", nullable = false)
  private long boardId;

  @Column(name = "school_id", nullable = false)
  private long schoolId;

  @Column(name = "relation_type", nullable = false, length = 100)
  private String relationType;

  @Column(nullable = false)
  private boolean active;

  protected BoardSchoolRelationEntity() {
  }

  public BoardSchoolRelationEntity(long boardId, long schoolId, String relationType, boolean active) {
    this.boardId = boardId;
    this.schoolId = schoolId;
    this.relationType = relationType;
    this.active = active;
  }

  public Long getId() {
    return id;
  }

  public long getBoardId() {
    return boardId;
  }

  public long getSchoolId() {
    return schoolId;
  }

  public String getRelationType() {
    return relationType;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
}
