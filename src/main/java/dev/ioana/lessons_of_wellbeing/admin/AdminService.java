package dev.ioana.lessons_of_wellbeing.admin;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.ioana.lessons_of_wellbeing.admin.dtos.AdminDashboardDTOResponse;
import dev.ioana.lessons_of_wellbeing.booking.BookingRepository;
import dev.ioana.lessons_of_wellbeing.classes.ClassRepository;
import dev.ioana.lessons_of_wellbeing.contracts.IAdminService;
import dev.ioana.lessons_of_wellbeing.enums.ReservationStatus;
import dev.ioana.lessons_of_wellbeing.enums.Role;
import dev.ioana.lessons_of_wellbeing.user.UserRepository;

@Service
public class AdminService implements IAdminService {
    private final ClassRepository classRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public AdminService(
        ClassRepository classRepository,
        BookingRepository bookingRepository,
        UserRepository userRepository
    ) {
        this.classRepository = classRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardDTOResponse getDashboard() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Madrid"));

        return new AdminDashboardDTOResponse(
            classRepository.count(),
            classRepository.countByStartDateTimeAfter(now),
            bookingRepository.countByStatus(ReservationStatus.CONFIRMED),
            bookingRepository.countByStatus(ReservationStatus.CANCELLED),
            userRepository.countByRole(Role.USER)
        );
    }
}
