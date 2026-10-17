package dev.ioana.lessons_of_wellbeing.booking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.server.ResponseStatusException;

import dev.ioana.lessons_of_wellbeing.booking.dtos.BookingDTORequest;
import dev.ioana.lessons_of_wellbeing.booking.dtos.BookingDTOResponse;
import dev.ioana.lessons_of_wellbeing.booking.dtos.UserBookingDTOResponse;
import dev.ioana.lessons_of_wellbeing.classes.ClassEntity;
import dev.ioana.lessons_of_wellbeing.classes.ClassRepository;
import dev.ioana.lessons_of_wellbeing.classes.dtos.ClassDTOResponse;
import dev.ioana.lessons_of_wellbeing.classes.mappers.ClassMapper;
import dev.ioana.lessons_of_wellbeing.contracts.IReservationService;
import dev.ioana.lessons_of_wellbeing.enums.ReservationStatus;
import dev.ioana.lessons_of_wellbeing.reservation.ReservationService;
import dev.ioana.lessons_of_wellbeing.reservation.dtos.ReservationDTORequest;
import dev.ioana.lessons_of_wellbeing.reservation.dtos.ReservationDTOResponse;
import dev.ioana.lessons_of_wellbeing.user.UserEntity;
import dev.ioana.lessons_of_wellbeing.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ClassRepository classRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private IReservationService reservationService;

    @Mock
    private ClassMapper classMapper;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void createBooking_savesBookingForSignedInUser() {
        UserEntity user = new UserEntity();
        ClassEntity classEntity = new ClassEntity();
        classEntity.setTitle("Kombucha Tea Tasting");
        BookingDTORequest request = createRequest("  Hibiscus Rose  ");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reservationService.validateReservation(eq(2L), any(ReservationDTORequest.class)))
            .thenReturn(new ReservationDTOResponse(2L, "Kombucha Tea Tasting", "Elena Vance",
                "elena@example.com", "+34 612 345 678", "Hibiscus Rose"));
        when(classRepository.findById(2L)).thenReturn(Optional.of(classEntity));
        when(bookingRepository.saveAndFlush(any(BookingEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingDTOResponse response = bookingService.createBooking("1", request);

        ArgumentCaptor<BookingEntity> captor = ArgumentCaptor.forClass(BookingEntity.class);
        verify(bookingRepository).saveAndFlush(captor.capture());
        BookingEntity saved = captor.getValue();
        assertThat(saved.getUser()).isSameAs(user);
        assertThat(saved.getClassEntity()).isSameAs(classEntity);
        assertThat(saved.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(saved.getNotes()).isEqualTo("Hibiscus Rose");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(response.sessionTitle()).isEqualTo("Kombucha Tea Tasting");
        assertThat(response.status()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(response.notes()).isEqualTo("Hibiscus Rose");
    }

    @Test
    void createBooking_rejectsSecondBookingOfSameClass() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new UserEntity()));
        when(reservationService.validateReservation(eq(2L), any(ReservationDTORequest.class)))
            .thenReturn(new ReservationDTOResponse(2L, "Kombucha Tea Tasting", "Elena Vance",
                "elena@example.com", "+34 612 345 678", "Hibiscus Rose"));
        when(bookingRepository.existsByUserIdAndClassEntityId(any(), eq(2L))).thenReturn(true);

        assertThatThrownBy(() -> bookingService.createBooking("1", createRequest("Hibiscus Rose")))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(exception -> {
                ResponseStatusException error = (ResponseStatusException) exception;
                assertThat(error.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                assertThat(error.getReason()).isEqualTo(BookingService.ALREADY_BOOKED_MESSAGE);
            });
        verify(bookingRepository, never()).saveAndFlush(any());
    }

    @Test
    void createBooking_storesEmptyNotesAsNull() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new UserEntity()));
        when(reservationService.validateReservation(eq(1L), any(ReservationDTORequest.class)))
            .thenReturn(new ReservationDTOResponse(1L, "Yoga", "Elena Vance",
                "elena@example.com", "+34 612 345 678", ""));
        when(classRepository.findById(1L)).thenReturn(Optional.of(new ClassEntity()));
        when(bookingRepository.saveAndFlush(any(BookingEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingDTOResponse response = bookingService.createBooking("1", new BookingDTORequest(
            1L, "Elena Vance", "elena@example.com", "+34 612 345 678", null));

        assertThat(response.notes()).isNull();
    }

    @Test
    void createBooking_doesNotSaveWhenNotesAreMissingForKombuchaTea() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new UserEntity()));
        when(reservationService.validateReservation(eq(2L), any(ReservationDTORequest.class)))
            .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, ReservationService.MISSING_NOTES_MESSAGE));

        assertThatThrownBy(() -> bookingService.createBooking("1", createRequest("")))
            .isInstanceOf(ResponseStatusException.class);
        verify(bookingRepository, never()).saveAndFlush(any());
    }

    @Test
    void createBooking_rejectsUnknownUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.createBooking("1", createRequest("Tea")))
            .isInstanceOf(UsernameNotFoundException.class);
        verify(bookingRepository, never()).saveAndFlush(any());
    }

    @Test
    void createBooking_rejectsInvalidSubject() {
        assertThatThrownBy(() -> bookingService.createBooking("not-a-number", createRequest("Tea")))
            .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void getMyBookings_returnsBookingsOfSignedInUserWithSessionDetails() {
        UserEntity user = new UserEntity();
        ClassEntity classEntity = new ClassEntity();
        BookingEntity booking = new BookingEntity();
        booking.setClassEntity(classEntity);
        booking.setStatus(ReservationStatus.CONFIRMED);
        booking.setNotes("ginger");
        ClassDTOResponse session = new ClassDTOResponse(1L, "Yoga", null, null, "Main Hall",
            null, 75, null, null, List.of(), List.of());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findByUserIdOrderByClassEntityStartDateTimeDesc(user.getId()))
            .thenReturn(List.of(booking));
        when(classMapper.toResponse(classEntity)).thenReturn(session);

        List<UserBookingDTOResponse> result = bookingService.getMyBookings("1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).status()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(result.get(0).notes()).isEqualTo("ginger");
        assertThat(result.get(0).session()).isSameAs(session);
    }

    @Test
    void getMyBookings_returnsEmptyListWhenUserHasNoBookings() {
        UserEntity user = new UserEntity();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findByUserIdOrderByClassEntityStartDateTimeDesc(user.getId()))
            .thenReturn(List.of());

        assertThat(bookingService.getMyBookings("1")).isEmpty();
    }

    @Test
    void getMyBookings_rejectsUnknownUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getMyBookings("1"))
            .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void cancelBooking_marksBookingAsCancelledAndSavesIt() {
        UserEntity user = new UserEntity();
        ClassEntity classEntity = new ClassEntity();
        BookingEntity booking = new BookingEntity();
        booking.setClassEntity(classEntity);
        booking.setStatus(ReservationStatus.CONFIRMED);
        ClassDTOResponse session = new ClassDTOResponse(1L, "Yoga", null, null, "Main Hall",
            null, 75, null, null, List.of(), List.of());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findByIdAndUserId(10L, user.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(classMapper.toResponse(classEntity)).thenReturn(session);

        UserBookingDTOResponse response = bookingService.cancelBooking("1", 10L);

        verify(bookingRepository).save(booking);
        assertThat(booking.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        assertThat(response.status()).isEqualTo(ReservationStatus.CANCELLED);
        assertThat(response.session()).isSameAs(session);
    }

    @Test
    void cancelBooking_throwsNotFoundForBookingOfAnotherUserOrUnknownBooking() {
        UserEntity user = new UserEntity();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findByIdAndUserId(99L, user.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.cancelBooking("1", 99L))
            .isInstanceOf(ResponseStatusException.class)
            .extracting(exception -> ((ResponseStatusException) exception).getStatusCode())
            .isEqualTo(HttpStatus.NOT_FOUND);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void cancelBooking_rejectsAlreadyCancelledBooking() {
        UserEntity user = new UserEntity();
        BookingEntity booking = new BookingEntity();
        booking.setStatus(ReservationStatus.CANCELLED);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findByIdAndUserId(10L, user.getId())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.cancelBooking("1", 10L))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(exception -> {
                ResponseStatusException error = (ResponseStatusException) exception;
                assertThat(error.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                assertThat(error.getReason()).isEqualTo(BookingService.ALREADY_CANCELLED_MESSAGE);
            });
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void cancelBooking_rejectsUnknownUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.cancelBooking("1", 10L))
            .isInstanceOf(UsernameNotFoundException.class);
    }

    private BookingDTORequest createRequest(String notes) {
        return new BookingDTORequest(2L, "Elena Vance", "elena@example.com", "+34 612 345 678", notes);
    }
}
