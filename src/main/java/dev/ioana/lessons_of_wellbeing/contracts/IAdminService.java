package dev.ioana.lessons_of_wellbeing.contracts;

import dev.ioana.lessons_of_wellbeing.admin.dtos.AdminDashboardDTOResponse;

public interface IAdminService {
    AdminDashboardDTOResponse getDashboard();
}
