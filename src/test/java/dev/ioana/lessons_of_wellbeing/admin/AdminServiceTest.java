package dev.ioana.lessons_of_wellbeing.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.ioana.lessons_of_wellbeing.admin.dtos.AdminDashboardDTOResponse;
import dev.ioana.lessons_of_wellbeing.booking.BookingRepository;
import dev.ioana.lessons_of_wellbeing.classes.ClassRepository;
import dev.ioana.lessons_of_wellbeing.enums.ReservationStatus;
import dev.ioana.lessons_of_wellbeing.enums.Role;
import dev.ioana.lessons_of_wellbeing.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {
    @Mock
    private ClassRepository classRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminService adminService;

    @Test
    void getDashboard_countsSessionsReservationsAndUsers() {
        when(classRepository.count()).thenReturn(8L);
        when(classRepository.countByStartDateTimeAfter(any(LocalDateTime.class))).thenReturn(6L);
        when(bookingRepository.countByStatus(ReservationStatus.CONFIRMED)).thenReturn(3L);
        when(bookingRepository.countByStatus(ReservationStatus.CANCELLED)).thenReturn(1L);
        when(userRepository.countByRole(Role.USER)).thenReturn(12L);

        AdminDashboardDTOResponse dashboard = adminService.getDashboard();

        assertThat(dashboard).isEqualTo(new AdminDashboardDTOResponse(8, 6, 3, 1, 12));
    }
}
