package nl.wunderbieb.kms.api.rs.docs;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
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
class DocumentControllerOidcIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void createAndVersionDocument_allowsEditorCapability() throws Exception {
    String createPayload = """
        {
          "documentTypeCode": "VISION_DOCUMENT",
          "title": "Testdocument",
          "contentJson": "{\\"type\\":\\"doc\\",\\"content\\":[{\\"type\\":\\"paragraph\\",\\"content\\":[{\\"type\\":\\"text\\",\\"text\\":\\"Eerste concept\\"}]}]}"
        }
        """;

    String createResponse = mockMvc.perform(post("/api/docs/documents")
            .contentType(MediaType.APPLICATION_JSON)
            .content(createPayload)
            .with(jwt().jwt(jwt -> jwt
                .subject("1")
                .claim("user_id", 1)
                .claim("role_code", "PLATFORM_ADMIN")
                .claim("scope_type", "PLATFORM"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.activeVersionNumber").value(1))
        .andReturn()
        .getResponse()
        .getContentAsString();

    String documentId = createResponse.replaceAll("(?s).*\"id\":(\\d+).*", "$1");

    mockMvc.perform(post("/api/docs/documents/" + documentId + "/versions")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "contentJson": "{\\"type\\":\\"doc\\",\\"content\\":[{\\"type\\":\\"paragraph\\",\\"content\\":[{\\"type\\":\\"text\\",\\"text\\":\\"Tweede concept\\"}]}]}",
                  "changeSummary": "Aanpassing voor review"
                }
                """)
            .with(jwt().jwt(jwt -> jwt
                .subject("1")
                .claim("user_id", 1)
                .claim("role_code", "PLATFORM_ADMIN")
                .claim("scope_type", "PLATFORM"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.activeVersionNumber").value(2))
        .andExpect(jsonPath("$.versions[0].versionNumber").value(2));
  }

  @Test
  void createDocument_deniesWhenCapabilityMissing() throws Exception {
    mockMvc.perform(post("/api/docs/documents")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "documentTypeCode": "VISION_DOCUMENT",
                  "title": "Niet toegestaan",
                  "contentJson": "{\\"type\\":\\"doc\\"}"
                }
                """)
            .with(jwt().jwt(jwt -> jwt
                .subject("43")
                .claim("user_id", 43)
                .claim("role_code", "TEAMLID")
                .claim("scope_type", "SCHOOL")
                .claim("board_id", 12)
                .claim("school_id", 42))))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("ACCESS_DENIED_CAPABILITY"))
        .andExpect(jsonPath("$.details.requiredCapability").value("EDIT_DOCUMENT"));
  }
}
