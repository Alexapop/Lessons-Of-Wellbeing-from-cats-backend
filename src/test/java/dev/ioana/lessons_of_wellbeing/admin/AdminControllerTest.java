package dev.ioana.lessons_of_wellbeing.admin;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import dev.ioana.lessons_of_wellbeing.admin.dtos.AdminDashboardDTOResponse;
import dev.ioana.lessons_of_wellbeing.contracts.IAdminService;
import dev.ioana.lessons_of_wellbeing.security.SecurityConfiguration;

@WebMvcTest(AdminController.class)
@Import(SecurityConfiguration.class)
class AdminControllerTest {
    @MockitoBean
    private org.springframework.security.oauth2.jwt.JwtDecoder jwtDecoder;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IAdminService adminService;

    @Test
    void getDashboard_returnsSummaryForAdmin() throws Exception {
        when(adminService.getDashboard()).thenReturn(new AdminDashboardDTOResponse(8, 6, 3, 1, 12));

        mockMvc.perform(get("/api/v1/admin/dashboard")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalSessions").value(8))
            .andExpect(jsonPath("$.upcomingSessions").value(6))
            .andExpect(jsonPath("$.confirmedReservations").value(3))
            .andExpect(jsonPath("$.cancelledReservations").value(1))
            .andExpect(jsonPath("$.registeredUsers").value(12));
    }

    @Test
    void getDashboard_isForbiddenForUsers() throws Exception {
        mockMvc.perform(get("/api/v1/admin/dashboard")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
            .andExpect(status().isForbidden());

        verifyNoInteractions(adminService);
    }

    @Test
    void getDashboard_isUnauthorizedWithoutLogin() throws Exception {
        mockMvc.perform(get("/api/v1/admin/dashboard"))
            .andExpect(status().isUnauthorized());

        verifyNoInteractions(adminService);
    }
}
