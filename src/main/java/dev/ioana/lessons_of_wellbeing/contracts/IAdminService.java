package dev.ioana.lessons_of_wellbeing.contracts;

import java.util.List;

import dev.ioana.lessons_of_wellbeing.admin.dtos.AdminBookingDTOResponse;
import dev.ioana.lessons_of_wellbeing.admin.dtos.AdminDashboardDTOResponse;

public interface IAdminService {
    AdminDashboardDTOResponse getDashboard();

    List<AdminBookingDTOResponse> getAllBookings();
}
