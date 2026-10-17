package dev.ioana.lessons_of_wellbeing.booking;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import dev.ioana.lessons_of_wellbeing.booking.dtos.BookingDTORequest;
import dev.ioana.lessons_of_wellbeing.booking.dtos.BookingDTOResponse;
import dev.ioana.lessons_of_wellbeing.booking.dtos.UserBookingDTOResponse;
import dev.ioana.lessons_of_wellbeing.classes.ClassEntity;
import dev.ioana.lessons_of_wellbeing.classes.ClassRepository;
import dev.ioana.lessons_of_wellbeing.classes.mappers.ClassMapper;
import dev.ioana.lessons_of_wellbeing.contracts.IBookingService;
import dev.ioana.lessons_of_wellbeing.contracts.IReservationService;
import dev.ioana.lessons_of_wellbeing.enums.ReservationStatus;
import dev.ioana.lessons_of_wellbeing.enums.Role;
import dev.ioana.lessons_of_wellbeing.reservation.dtos.ReservationDTOResponse;
import dev.ioana.lessons_of_wellbeing.user.UserEntity;
import dev.ioana.lessons_of_wellbeing.user.UserRepository;

@Service
public class BookingService implements IBookingService {
    public static final String ALREADY_BOOKED_MESSAGE = "You have already booked this session.";
    public static final String ALREADY_CANCELLED_MESSAGE = "This booking is already cancelled.";
    public static final String ADMIN_CANNOT_BOOK_MESSAGE = "Admins cannot book sessions.";

    private final BookingRepository bookingRepository;
    private final ClassRepository classRepository;
    private final UserRepository userRepository;
    private final IReservationService reservationService;
    private final ClassMapper classMapper;

    public BookingService(
        BookingRepository bookingRepository,
        ClassRepository classRepository,
        UserRepository userRepository,
        IReservationService reservationService,
        ClassMapper classMapper
    ) {
        this.bookingRepository = bookingRepository;
        this.classRepository = classRepository;
        this.userRepository = userRepository;
        this.reservationService = reservationService;
        this.classMapper = classMapper;
    }

    @Override
    @Transactional
    public BookingDTOResponse createBooking(String userSubject, BookingDTORequest request) {
        UserEntity user = findUser(userSubject);

        // Only members book sessions; admins manage them.
        if (user.getRole() == Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ADMIN_CANNOT_BOOK_MESSAGE);
        }

        // Reuses the reservation rules: 404 for unknown sessions, notes required for Kombucha/Tea.
        ReservationDTOResponse validated =
            reservationService.validateReservation(request.sessionId(), request.toReservationRequest());

        if (bookingRepository.existsByUserIdAndClassEntityId(user.getId(), request.sessionId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ALREADY_BOOKED_MESSAGE);
        }

        ClassEntity classEntity = classRepository.findById(request.sessionId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        BookingEntity booking = new BookingEntity();
        booking.setUser(user);
        booking.setClassEntity(classEntity);
        booking.setStatus(ReservationStatus.CONFIRMED);
        booking.setNotes(validated.notes().isEmpty() ? null : validated.notes());
        booking.setCreatedAt(LocalDateTime.now(ZoneId.of("Europe/Madrid")));

        BookingEntity saved;
        try {
            saved = bookingRepository.saveAndFlush(booking);
        } catch (DataIntegrityViolationException exception) {
            // The unique (user_id, class_id) constraint also protects simultaneous bookings.
            throw new ResponseStatusException(HttpStatus.CONFLICT, ALREADY_BOOKED_MESSAGE);
        }

        return new BookingDTOResponse(
            saved.getId(),
            classEntity.getId(),
            classEntity.getTitle(),
            classEntity.getStartDateTime(),
            user.getId(),
            saved.getStatus(),
            saved.getNotes(),
            saved.getCreatedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserBookingDTOResponse> getMyBookings(String userSubject) {
        UserEntity user = findUser(userSubject);

        List<BookingEntity> bookings = bookingRepository.findByUserIdOrderByClassEntityStartDateTimeDesc(user.getId());
        List<UserBookingDTOResponse> responses = new ArrayList<>();

        for (BookingEntity booking : bookings) {
            responses.add(toUserBookingResponse(booking));
        }

        return responses;
    }

    @Override
    @Transactional
    public UserBookingDTOResponse cancelBooking(String userSubject, Long bookingId) {
        UserEntity user = findUser(userSubject);

        // Bookings of other users are reported as not found, so their ids are not revealed.
        BookingEntity booking = bookingRepository.findByIdAndUserId(bookingId, user.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (booking.getStatus() == ReservationStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ALREADY_CANCELLED_MESSAGE);
        }

        // The booking is kept and only marked as cancelled.
        booking.setStatus(ReservationStatus.CANCELLED);
        BookingEntity saved = bookingRepository.save(booking);

        return toUserBookingResponse(saved);
    }

    private UserBookingDTOResponse toUserBookingResponse(BookingEntity booking) {
        return new UserBookingDTOResponse(
            booking.getId(),
            booking.getStatus(),
            booking.getNotes(),
            booking.getCreatedAt(),
            classMapper.toResponse(booking.getClassEntity())
        );
    }

    private UserEntity findUser(String userSubject) {
        try {
            return userRepository.findById(Long.valueOf(userSubject))
                .orElseThrow(() -> new UsernameNotFoundException("User no longer exists."));
        } catch (NumberFormatException exception) {
            throw new UsernameNotFoundException("User no longer exists.");
        }
    }
}
