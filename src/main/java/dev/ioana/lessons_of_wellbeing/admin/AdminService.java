package dev.ioana.lessons_of_wellbeing.admin;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.ioana.lessons_of_wellbeing.admin.dtos.AdminBookingDTOResponse;
import dev.ioana.lessons_of_wellbeing.admin.dtos.AdminDashboardDTOResponse;
import dev.ioana.lessons_of_wellbeing.booking.BookingEntity;
import dev.ioana.lessons_of_wellbeing.booking.BookingRepository;
import dev.ioana.lessons_of_wellbeing.classes.ClassRepository;
import dev.ioana.lessons_of_wellbeing.classes.mappers.ClassMapper;
import dev.ioana.lessons_of_wellbeing.contracts.IAdminService;
import dev.ioana.lessons_of_wellbeing.enums.ReservationStatus;
import dev.ioana.lessons_of_wellbeing.enums.Role;
import dev.ioana.lessons_of_wellbeing.user.UserEntity;
import dev.ioana.lessons_of_wellbeing.user.UserRepository;

@Service
public class AdminService implements IAdminService {
    private final ClassRepository classRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ClassMapper classMapper;

    public AdminService(
        ClassRepository classRepository,
        BookingRepository bookingRepository,
        UserRepository userRepository,
        ClassMapper classMapper
    ) {
        this.classRepository = classRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.classMapper = classMapper;
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

    @Override
    @Transactional(readOnly = true)
    public List<AdminBookingDTOResponse> getAllBookings() {
        List<BookingEntity> bookings = bookingRepository.findAllByOrderByClassEntityStartDateTimeDesc();
        List<AdminBookingDTOResponse> responses = new ArrayList<>();

        for (BookingEntity booking : bookings) {
            UserEntity user = booking.getUser();

            // Only the contact data the admin needs, never the password hash.
            AdminBookingDTOResponse.BookingUserResponse userResponse =
                new AdminBookingDTOResponse.BookingUserResponse(
                    user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getPhoneNumber()
                );

            responses.add(new AdminBookingDTOResponse(
                booking.getId(),
                booking.getStatus(),
                booking.getNotes(),
                booking.getCreatedAt(),
                userResponse,
                classMapper.toResponse(booking.getClassEntity())
            ));
        }

        return responses;
    }
}
