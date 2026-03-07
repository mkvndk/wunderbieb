package nl.wunderbieb.kms.api.rs.insights;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {
    "kms.security.oidc-enabled=true",
    "kms.security.debug-role-header-enabled=false",
    "kms.security.oidc-jwk-set-uri=http://localhost/mock/jwks"
})
@AutoConfigureMockMvc
class InsightsControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void domainScores_areAggregatedFromMappedDocumentsAndDomains() throws Exception {
    String createResponse = mockMvc.perform(post("/api/docs/documents")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "documentTypeCode": "VISION_DOCUMENT",
                  "title": "Kwaliteitsanalyse SKA",
                  "summary": "Onderbouwing voor kwaliteitssturing",
                  "contentJson": "{\\"type\\":\\"doc\\"}"
                }
                """)
            .with(jwt().jwt(jwt -> jwt
                .subject("1")
                .claim("user_id", 1)
                .claim("role_code", "PLATFORM_ADMIN")
                .claim("scope_type", "PLATFORM"))))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    String documentId = createResponse.replaceAll("(?s).*\"id\":(\\d+).*", "$1");

    mockMvc.perform(post("/api/admin/documents/" + documentId + "/topic-mappings")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "topicCode": "SKA2",
                  "mappingSource": "MANUAL_ADMIN"
                }
                """)
            .with(jwt().jwt(jwt -> jwt
                .subject("1")
                .claim("user_id", 1)
                .claim("role_code", "PLATFORM_ADMIN")
                .claim("scope_type", "PLATFORM"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.documentId").value(Integer.parseInt(documentId)));

    mockMvc.perform(post("/api/docs/documents/" + documentId + "/review")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "versionNumber": 1
                }
                """)
            .with(jwt().jwt(jwt -> jwt
                .subject("1")
                .claim("user_id", 1)
                .claim("role_code", "PLATFORM_ADMIN")
                .claim("scope_type", "PLATFORM"))))
        .andExpect(status().isOk());

    mockMvc.perform(post("/api/docs/documents/" + documentId + "/approve")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "versionNumber": 1
                }
                """)
            .with(jwt().jwt(jwt -> jwt
                .subject("1")
                .claim("user_id", 1)
                .claim("role_code", "PLATFORM_ADMIN")
                .claim("scope_type", "PLATFORM"))))
        .andExpect(status().isOk());

    mockMvc.perform(get("/api/insights/domain-scores")
            .param("trendPeriodDays", "30")
            .with(jwt().jwt(jwt -> jwt
                .subject("1")
                .claim("user_id", 1)
                .claim("role_code", "PLATFORM_ADMIN")
                .claim("scope_type", "PLATFORM"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.trendPeriodDays").value(30))
        .andExpect(jsonPath("$.domains[0].domainCode").value("SKA"))
        .andExpect(jsonPath("$.domains[0].supportingDocumentCount").value(1))
        .andExpect(jsonPath("$.domains[0].supportingDocuments[0].documentId").value(Integer.parseInt(documentId)))
        .andExpect(jsonPath("$.domains[0].trendPeriodDays").value(30));
  }

  @Test
  void domainScores_rejectInvalidTrendPeriod() throws Exception {
    mockMvc.perform(get("/api/insights/domain-scores")
            .param("trendPeriodDays", "0")
            .with(jwt().jwt(jwt -> jwt
                .subject("1")
                .claim("user_id", 1)
                .claim("role_code", "PLATFORM_ADMIN")
                .claim("scope_type", "PLATFORM"))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
  }
}
