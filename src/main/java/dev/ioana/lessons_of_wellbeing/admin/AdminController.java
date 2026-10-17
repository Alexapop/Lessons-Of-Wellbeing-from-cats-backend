package dev.ioana.lessons_of_wellbeing.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.ioana.lessons_of_wellbeing.admin.dtos.AdminDashboardDTOResponse;
import dev.ioana.lessons_of_wellbeing.contracts.IAdminService;

// Every /admin endpoint requires ROLE_ADMIN (see SecurityConfiguration).
@RestController
@RequestMapping(path = "${api-endpoint}/admin")
public class AdminController {
    private final IAdminService adminService;

    public AdminController(IAdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    public AdminDashboardDTOResponse getDashboard() {
        return adminService.getDashboard();
    }
}
