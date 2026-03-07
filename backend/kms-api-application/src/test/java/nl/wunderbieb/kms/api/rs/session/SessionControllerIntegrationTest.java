package nl.wunderbieb.kms.api.rs.session;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
class SessionControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void currentSession_returnsResolvedAssignmentAndJwtClaims() throws Exception {
    mockMvc.perform(get("/api/session")
            .with(jwt().jwt(jwt -> jwt
                .subject("1")
                .claim("user_id", 1)
                .claim("role_code", "PLATFORM_ADMIN")
                .claim("scope_type", "PLATFORM")
                .claim("preferred_username", "super-admin")
                .claim("name", "Super Admin")
                .claim("email", "super-admin@wunderbieb.local"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(1))
        .andExpect(jsonPath("$.preferredUsername").value("super-admin"))
        .andExpect(jsonPath("$.roleCode").value("PLATFORM_ADMIN"))
        .andExpect(jsonPath("$.capabilities").isArray());
  }
}
