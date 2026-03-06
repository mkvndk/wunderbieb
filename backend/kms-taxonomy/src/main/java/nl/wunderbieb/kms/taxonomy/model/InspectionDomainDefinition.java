package nl.wunderbieb.kms.taxonomy.model;

import java.util.List;

public record InspectionDomainDefinition(
    long id,
    String code,
    String displayNameNl,
    String descriptionNl,
    int sortOrder,
    boolean active,
    List<InspectionTopicDefinition> topics
) {

  public InspectionDomainDefinition {
    topics = topics == null ? List.of() : List.copyOf(topics);
  }
}
