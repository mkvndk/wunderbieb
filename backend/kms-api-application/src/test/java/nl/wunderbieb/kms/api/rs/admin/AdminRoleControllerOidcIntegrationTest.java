package nl.wunderbieb.kms.api.rs.admin;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {
    "kms.security.oidc-enabled=true",
    "kms.security.debug-role-header-enabled=false",
    "kms.security.oidc-jwk-set-uri=http://localhost/mock/jwks"
})
@AutoConfigureMockMvc
class AdminRoleControllerOidcIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void listRoles_allowsResolvedPlatformAdminAssignment() throws Exception {
    mockMvc.perform(get("/api/admin/roles")
            .with(jwt().jwt(jwt -> jwt
                .subject("1")
                .claim("user_id", 1)
                .claim("role_code", "PLATFORM_ADMIN")
                .claim("scope_type", "PLATFORM"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].code").value("PLATFORM_ADMIN"));
  }

  @Test
  void listRoles_rejectsAuthenticatedJwtWithoutResolvableAssignment() throws Exception {
    mockMvc.perform(get("/api/admin/roles")
            .with(jwt().jwt(jwt -> jwt
                .subject("9999")
                .claim("user_id", 9999)
                .claim("role_code", "PLATFORM_ADMIN")
                .claim("scope_type", "PLATFORM"))))
        .andExpect(status().isUnauthorized())
        .andExpect(header().exists("X-Trace-Id"))
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }
}
