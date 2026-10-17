package dev.ioana.lessons_of_wellbeing.contracts;

import java.util.List;

import dev.ioana.lessons_of_wellbeing.admin.dtos.AdminBookingDTOResponse;
import dev.ioana.lessons_of_wellbeing.admin.dtos.AdminDashboardDTOResponse;
import dev.ioana.lessons_of_wellbeing.admin.dtos.CreateSessionDTORequest;
import dev.ioana.lessons_of_wellbeing.admin.dtos.SessionOptionsDTOResponse;
import dev.ioana.lessons_of_wellbeing.classes.dtos.ClassDTOResponse;

public interface IAdminService {
    AdminDashboardDTOResponse getDashboard();

    List<AdminBookingDTOResponse> getAllBookings();

    SessionOptionsDTOResponse getSessionOptions();

    ClassDTOResponse createSession(CreateSessionDTORequest request);

    ClassDTOResponse updateSession(Long id, CreateSessionDTORequest request);

    void deleteSession(Long id);
}
