package nl.wunderbieb.kms.api.rs.search.dto;

import java.util.List;

public record SearchResponse(
    String query,
    String documentTypeCode,
    String workflowStatus,
    Long schoolId,
    String domainCode,
    List<SearchDocumentResult> documents,
    List<SearchTopicResult> topics,
    List<SearchDocumentTypeResult> documentTypes
) {
}
