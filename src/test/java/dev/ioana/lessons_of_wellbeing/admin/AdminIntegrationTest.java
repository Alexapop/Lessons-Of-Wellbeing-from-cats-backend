package dev.ioana.lessons_of_wellbeing.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import dev.ioana.lessons_of_wellbeing.enums.Role;
import dev.ioana.lessons_of_wellbeing.user.UserEntity;
import dev.ioana.lessons_of_wellbeing.user.UserRepository;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest(properties = {
    "jwt.secret=YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY=",
    "demo.user.password=",
    "demo.admin.email=admin-it@example.com",
    "demo.admin.password=AdminPassword123!",
    "spring.datasource.url=jdbc:h2:mem:admin-tests"
})
@AutoConfigureMockMvc
class AdminIntegrationTest {
    private static final String ADMIN_EMAIL = "admin-it@example.com";
    private static final String ADMIN_PASSWORD = "AdminPassword123!";
    private static final String USER_EMAIL = "plain-user@example.com";
    private static final String USER_PASSWORD = "TestPassword123!";

    @Autowired private MockMvc mvc;
    @Autowired private UserRepository users;
    @Autowired private PasswordEncoder encoder;
    @Autowired private ObjectMapper mapper;
    @Autowired private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        if (users.findByEmailIgnoreCase(USER_EMAIL).isEmpty()) {
            UserEntity user = new UserEntity();
            user.setFirstName("Plain");
            user.setLastName("User");
            user.setEmail(USER_EMAIL);
            user.setPassword(encoder.encode(USER_PASSWORD));
            users.save(user);
        }
    }

    private String login(String email, String password, String expectedRole) throws Exception {
        String body = mvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.user.role").value(expectedRole))
            .andReturn().getResponse().getContentAsString();
        return mapper.readTree(body).get("accessToken").asString();
    }

    @Test
    void demoAdminIsCreatedWithAdminRole() {
        assertThat(users.findByEmailIgnoreCase(ADMIN_EMAIL)).get()
            .extracting(UserEntity::getRole).isEqualTo(Role.ADMIN);
    }

    @Test
    void adminLoginPutsAdminRoleInJwtAndOpensAdminDashboard() throws Exception {
        String token = login(ADMIN_EMAIL, ADMIN_PASSWORD, "ADMIN");

        Jwt jwt = jwtDecoder.decode(token);
        assertThat(jwt.getClaimAsStringList("roles")).containsExactly("ADMIN");

        mvc.perform(get("/api/v1/admin/dashboard").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalSessions").isNumber())
            .andExpect(jsonPath("$.registeredUsers").isNumber());
    }

    @Test
    void userTokenIsForbiddenOnAdminEndpoints() throws Exception {
        String token = login(USER_EMAIL, USER_PASSWORD, "USER");

        assertThat(jwtDecoder.decode(token).getClaimAsStringList("roles")).containsExactly("USER");

        mvc.perform(get("/api/v1/admin/dashboard").header("Authorization", "Bearer " + token))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void adminEndpointsRequireLogin() throws Exception {
        mvc.perform(get("/api/v1/admin/dashboard"))
            .andExpect(status().isUnauthorized());
    }
}
