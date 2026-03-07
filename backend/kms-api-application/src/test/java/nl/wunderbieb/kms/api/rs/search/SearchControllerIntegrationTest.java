package nl.wunderbieb.kms.api.rs.search;

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
class SearchControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void search_returnsDocumentsTopicsAndDocumentTypes() throws Exception {
    mockMvc.perform(post("/api/docs/documents")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "documentTypeCode": "VISION_DOCUMENT",
                  "title": "Visieplan 2026",
                  "summary": "Visie en ambities voor schooljaar",
                  "contentJson": "{\\"type\\":\\"doc\\"}"
                }
                """)
            .with(jwt().jwt(jwt -> jwt
                .subject("1")
                .claim("user_id", 1)
                .claim("role_code", "PLATFORM_ADMIN")
                .claim("scope_type", "PLATFORM"))))
        .andExpect(status().isOk());

    mockMvc.perform(get("/api/search")
            .param("query", "visie")
            .with(jwt().jwt(jwt -> jwt
                .subject("1")
                .claim("user_id", 1)
                .claim("role_code", "PLATFORM_ADMIN")
                .claim("scope_type", "PLATFORM"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.documents[0].title").value("Visieplan 2026"))
        .andExpect(jsonPath("$.topics[0].domainCode").exists())
        .andExpect(jsonPath("$.documentTypes[0].code").exists());
  }

  @Test
  void search_filtersByWorkflowSchoolAndDomain() throws Exception {
    mockMvc.perform(post("/api/docs/documents")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "documentTypeCode": "QUALITY_CARD",
                  "title": "School 42 kwaliteitskaart",
                  "summary": "Specifiek voor schoolcontext",
                  "contentJson": "{\\"type\\":\\"doc\\"}"
                }
                """)
            .with(jwt().jwt(jwt -> jwt
                .subject("42")
                .claim("user_id", 42)
                .claim("role_code", "DIRECTEUR")
                .claim("scope_type", "SCHOOL")
                .claim("board_id", 12)
                .claim("school_id", 42))))
        .andExpect(status().isOk());

    mockMvc.perform(get("/api/search")
            .param("workflowStatus", "DRAFT")
            .param("schoolId", "42")
            .param("domainCode", "SKA")
            .with(jwt().jwt(jwt -> jwt
                .subject("1")
                .claim("user_id", 1)
                .claim("role_code", "PLATFORM_ADMIN")
                .claim("scope_type", "PLATFORM"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.documents[0].schoolId").value(42))
        .andExpect(jsonPath("$.documents[0].workflowStatus").value("DRAFT"))
        .andExpect(jsonPath("$.topics.length()").value(2))
        .andExpect(jsonPath("$.topics[0].domainCode").value("SKA"));
  }

  @Test
  void search_rejectsInvalidWorkflowStatus() throws Exception {
    mockMvc.perform(get("/api/search")
            .param("workflowStatus", "ONGELDIG")
            .with(jwt().jwt(jwt -> jwt
                .subject("1")
                .claim("user_id", 1)
                .claim("role_code", "PLATFORM_ADMIN")
                .claim("scope_type", "PLATFORM"))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
  }
}
