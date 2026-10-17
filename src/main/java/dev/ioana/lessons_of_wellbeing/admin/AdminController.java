package dev.ioana.lessons_of_wellbeing.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.ioana.lessons_of_wellbeing.admin.dtos.AdminBookingDTOResponse;
import dev.ioana.lessons_of_wellbeing.admin.dtos.AdminDashboardDTOResponse;
import dev.ioana.lessons_of_wellbeing.admin.dtos.CreateSessionDTORequest;
import dev.ioana.lessons_of_wellbeing.admin.dtos.SessionOptionsDTOResponse;
import dev.ioana.lessons_of_wellbeing.classes.dtos.ClassDTOResponse;
import dev.ioana.lessons_of_wellbeing.contracts.IAdminService;
import jakarta.validation.Valid;

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

    // All reservations of all users, newest session first.
    @GetMapping("/bookings")
    public List<AdminBookingDTOResponse> getAllBookings() {
        return adminService.getAllBookings();
    }

    // Categories, levels, guides and hosts for the session creation form.
    @GetMapping("/session-options")
    public SessionOptionsDTOResponse getSessionOptions() {
        return adminService.getSessionOptions();
    }

    @PostMapping("/sessions")
    public ResponseEntity<ClassDTOResponse> createSession(@Valid @RequestBody CreateSessionDTORequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createSession(request));
    }

    // Uses the same rules as creating a session; 404 when the session does not exist.
    @PutMapping("/sessions/{id}")
    public ClassDTOResponse updateSession(
        @PathVariable Long id,
        @Valid @RequestBody CreateSessionDTORequest request
    ) {
        return adminService.updateSession(id, request);
    }

    // Logical delete: hides the session from the calendar and cancels its confirmed reservations.
    @DeleteMapping("/sessions/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        adminService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }
}
