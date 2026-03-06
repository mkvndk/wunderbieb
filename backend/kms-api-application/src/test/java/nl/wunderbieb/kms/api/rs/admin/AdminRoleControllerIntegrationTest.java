package nl.wunderbieb.kms.api.rs.admin;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AdminRoleControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void listRoles_requiresSuperAdminHeader() throws Exception {
    mockMvc.perform(get("/api/admin/roles"))
        .andExpect(status().isUnauthorized())
        .andExpect(header().exists("X-Trace-Id"))
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void createRole_allowsPlatformAdmin() throws Exception {
    mockMvc.perform(post("/api/admin/roles")
            .header("X-Demo-Role-Code", "PLATFORM_ADMIN")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "code": "EXTERN_AUDITOR",
                  "displayNameNl": "Externe auditor",
                  "descriptionNl": "Auditrol",
                  "scopeType": "EXTERN",
                  "permissionLevel": "READ",
                  "capabilityCodes": ["EXPORT_DATA"]
                }
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("EXTERN_AUDITOR"))
        .andExpect(jsonPath("$.capabilityCodes", hasSize(1)));
  }
}
