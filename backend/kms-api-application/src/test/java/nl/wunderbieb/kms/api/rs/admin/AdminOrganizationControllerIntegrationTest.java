package nl.wunderbieb.kms.api.rs.admin;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class AdminOrganizationControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void listBoards_requiresSuperAdminHeader() throws Exception {
    mockMvc.perform(get("/api/admin/boards"))
        .andExpect(status().isUnauthorized())
        .andExpect(header().exists("X-Trace-Id"))
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void listBoards_rejectsRoleWithoutManageOrgCapability() throws Exception {
    mockMvc.perform(get("/api/admin/boards")
            .header("X-Demo-Role-Code", "DIRECTEUR"))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("ACCESS_DENIED_CAPABILITY"))
        .andExpect(jsonPath("$.details.requiredCapability").value("MANAGE_ORG"));
  }

  @Test
  void createBoardAndSchool_thenPatchSchool_andCreateRelation() throws Exception {
    long boardId = createBoard("BOARD_P4_A", "Bestuur Punt 4 A");
    long schoolId = createSchool("P4001", "School Punt 4 A");

    mockMvc.perform(patch("/api/admin/schools/{schoolId}", schoolId)
            .header("X-Demo-Role-Code", "PLATFORM_ADMIN")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "displayNameNl": "School Punt 4 A Bijgewerkt",
                  "active": false
                }
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(schoolId))
        .andExpect(jsonPath("$.displayNameNl").value("School Punt 4 A Bijgewerkt"))
        .andExpect(jsonPath("$.active").value(false));

    mockMvc.perform(post("/api/admin/board-school-relations")
            .header("X-Demo-Role-Code", "PLATFORM_ADMIN")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "boardId": %d,
                  "schoolId": %d,
                  "relationType": "PRIMARY"
                }
                """.formatted(boardId, schoolId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.boardId").value(boardId))
        .andExpect(jsonPath("$.schoolId").value(schoolId))
        .andExpect(jsonPath("$.relationType").value("PRIMARY"))
        .andExpect(jsonPath("$.active").value(true));
  }

  @Test
  void createBoardSchoolRelation_rejectsDuplicateRelation() throws Exception {
    long boardId = createBoard("BOARD_P4_B", "Bestuur Punt 4 B");
    long schoolId = createSchool("P4002", "School Punt 4 B");

    mockMvc.perform(post("/api/admin/board-school-relations")
            .header("X-Demo-Role-Code", "PLATFORM_ADMIN")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "boardId": %d,
                  "schoolId": %d,
                  "relationType": "PRIMARY"
                }
                """.formatted(boardId, schoolId)))
        .andExpect(status().isOk());

    mockMvc.perform(post("/api/admin/board-school-relations")
            .header("X-Demo-Role-Code", "PLATFORM_ADMIN")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "boardId": %d,
                  "schoolId": %d,
                  "relationType": "PRIMARY"
                }
                """.formatted(boardId, schoolId)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
  }

  @Test
  void createBoardSchoolRelation_rejectsUnknownSchool() throws Exception {
    long boardId = createBoard("BOARD_P4_C", "Bestuur Punt 4 C");

    mockMvc.perform(post("/api/admin/board-school-relations")
            .header("X-Demo-Role-Code", "PLATFORM_ADMIN")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "boardId": %d,
                  "schoolId": 9999,
                  "relationType": "PRIMARY"
                }
                """.formatted(boardId)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
  }

  private long createBoard(String code, String displayNameNl) throws Exception {
    MvcResult result = mockMvc.perform(post("/api/admin/boards")
            .header("X-Demo-Role-Code", "PLATFORM_ADMIN")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "code": "%s",
                  "displayNameNl": "%s"
                }
                """.formatted(code, displayNameNl)))
        .andExpect(status().isOk())
        .andReturn();
    JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
    return json.get("id").asLong();
  }

  private long createSchool(String brin, String displayNameNl) throws Exception {
    MvcResult result = mockMvc.perform(post("/api/admin/schools")
            .header("X-Demo-Role-Code", "PLATFORM_ADMIN")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "brin": "%s",
                  "displayNameNl": "%s"
                }
                """.formatted(brin, displayNameNl)))
        .andExpect(status().isOk())
        .andReturn();
    JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
    return json.get("id").asLong();
  }
}
