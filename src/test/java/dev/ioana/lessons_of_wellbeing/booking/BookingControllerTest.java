package dev.ioana.lessons_of_wellbeing.booking;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import dev.ioana.lessons_of_wellbeing.booking.dtos.BookingDTORequest;
import dev.ioana.lessons_of_wellbeing.booking.dtos.BookingDTOResponse;
import dev.ioana.lessons_of_wellbeing.booking.dtos.UserBookingDTOResponse;
import dev.ioana.lessons_of_wellbeing.classes.dtos.ClassDTOResponse;
import dev.ioana.lessons_of_wellbeing.enums.CategoryName;
import dev.ioana.lessons_of_wellbeing.enums.ClassLevel;
import dev.ioana.lessons_of_wellbeing.contracts.IBookingService;
import dev.ioana.lessons_of_wellbeing.enums.ReservationStatus;
import dev.ioana.lessons_of_wellbeing.reservation.ReservationService;
import dev.ioana.lessons_of_wellbeing.security.SecurityConfiguration;

@WebMvcTest(BookingController.class)
@Import(SecurityConfiguration.class)
class BookingControllerTest {
    @MockitoBean
    private org.springframework.security.oauth2.jwt.JwtDecoder jwtDecoder;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IBookingService bookingService;

    private static String body(String notes) {
        return "{\"sessionId\":2,\"name\":\"Elena Vance\",\"email\":\"elena@example.com\","
            + "\"phone\":\"+34 612 345 678\",\"notes\":\"" + notes + "\"}";
    }

    @Test
    void createBooking_returnsCreatedBookingForSignedInUser() throws Exception {
        when(bookingService.createBooking(eq("1"), any(BookingDTORequest.class)))
            .thenReturn(new BookingDTOResponse(10L, 2L, "Kombucha Tea Tasting",
                LocalDateTime.of(2026, 10, 20, 16, 30), 1L, ReservationStatus.CONFIRMED,
                "Hibiscus Rose", LocalDateTime.of(2026, 10, 17, 12, 0)));

        mockMvc.perform(post("/api/v1/bookings").with(jwt().jwt(token -> token.subject("1")))
                .contentType(MediaType.APPLICATION_JSON).content(body("Hibiscus Rose")))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(10))
            .andExpect(jsonPath("$.sessionId").value(2))
            .andExpect(jsonPath("$.sessionTitle").value("Kombucha Tea Tasting"))
            .andExpect(jsonPath("$.status").value("CONFIRMED"))
            .andExpect(jsonPath("$.notes").value("Hibiscus Rose"));
    }

    @Test
    void createBooking_returnsConflictWhenClassIsAlreadyBooked() throws Exception {
        when(bookingService.createBooking(eq("1"), any(BookingDTORequest.class)))
            .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, BookingService.ALREADY_BOOKED_MESSAGE));

        mockMvc.perform(post("/api/v1/bookings").with(jwt().jwt(token -> token.subject("1")))
                .contentType(MediaType.APPLICATION_JSON).content(body("Hibiscus Rose")))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value(BookingService.ALREADY_BOOKED_MESSAGE));
    }

    @Test
    void createBooking_returnsBadRequestWhenNotesAreMissingForKombuchaTea() throws Exception {
        when(bookingService.createBooking(eq("1"), any(BookingDTORequest.class)))
            .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, ReservationService.MISSING_NOTES_MESSAGE));

        mockMvc.perform(post("/api/v1/bookings").with(jwt().jwt(token -> token.subject("1")))
                .contentType(MediaType.APPLICATION_JSON).content(body("")))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(ReservationService.MISSING_NOTES_MESSAGE));
    }

    @Test
    void createBooking_returnsBadRequestWhenRequiredFieldsAreMissing() throws Exception {
        mockMvc.perform(post("/api/v1/bookings").with(jwt().jwt(token -> token.subject("1")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\",\"email\":\"not-an-email\",\"phone\":\"abc\"}"))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
    }

    @Test
    void getMyBookings_returnsBookingsOfSignedInUser() throws Exception {
        ClassDTOResponse session = new ClassDTOResponse(1L, "Queen Mia Restorative Cat Nap Yoga & Pranayama",
            "Restorative yoga.", ClassLevel.GENTLE_RESTORATIVE, "The Velvet Sun Lounge (Main Hall)",
            LocalDateTime.of(2026, 10, 12, 7, 30), 75,
            new ClassDTOResponse.CategoryResponse(1L, CategoryName.YOGA),
            new ClassDTOResponse.GuideResponse(1L, "Aria"),
            List.of(new ClassDTOResponse.HostResponse(1L, "Mia")), List.of());
        when(bookingService.getMyBookings("1")).thenReturn(List.of(new UserBookingDTOResponse(
            10L, ReservationStatus.CONFIRMED, "ginger", LocalDateTime.of(2026, 10, 1, 9, 0), session)));

        mockMvc.perform(get("/api/v1/bookings/me").with(jwt().jwt(token -> token.subject("1"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(10))
            .andExpect(jsonPath("$[0].status").value("CONFIRMED"))
            .andExpect(jsonPath("$[0].notes").value("ginger"))
            .andExpect(jsonPath("$[0].session.location").value("The Velvet Sun Lounge (Main Hall)"))
            .andExpect(jsonPath("$[0].session.startDateTime").value("2026-10-12T07:30:00"))
            .andExpect(jsonPath("$[0].session.hosts[0].name").value("Mia"));
    }

    @Test
    void cancelBooking_returnsCancelledBooking() throws Exception {
        ClassDTOResponse session = new ClassDTOResponse(1L, "Queen Mia Restorative Cat Nap Yoga & Pranayama",
            "Restorative yoga.", ClassLevel.GENTLE_RESTORATIVE, "The Velvet Sun Lounge (Main Hall)",
            LocalDateTime.of(2026, 10, 12, 7, 30), 75,
            new ClassDTOResponse.CategoryResponse(1L, CategoryName.YOGA),
            new ClassDTOResponse.GuideResponse(1L, "Aria"),
            List.of(new ClassDTOResponse.HostResponse(1L, "Mia")), List.of());
        when(bookingService.cancelBooking("1", 10L)).thenReturn(new UserBookingDTOResponse(
            10L, ReservationStatus.CANCELLED, "ginger", LocalDateTime.of(2026, 10, 1, 9, 0), session));

        mockMvc.perform(delete("/api/v1/bookings/10").with(jwt().jwt(token -> token.subject("1"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(10))
            .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void cancelBooking_returnsNotFoundForUnknownBooking() throws Exception {
        when(bookingService.cancelBooking("1", 99L)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(delete("/api/v1/bookings/99").with(jwt().jwt(token -> token.subject("1"))))
            .andExpect(status().isNotFound());
    }

    @Test
    void cancelBooking_returnsConflictWhenAlreadyCancelled() throws Exception {
        when(bookingService.cancelBooking("1", 10L))
            .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, BookingService.ALREADY_CANCELLED_MESSAGE));

        mockMvc.perform(delete("/api/v1/bookings/10").with(jwt().jwt(token -> token.subject("1"))))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value(BookingService.ALREADY_CANCELLED_MESSAGE));
    }

    @Test
    void cancelBooking_isUnauthorizedWithoutLogin() throws Exception {
        mockMvc.perform(delete("/api/v1/bookings/10"))
            .andExpect(status().isUnauthorized());

        verifyNoInteractions(bookingService);
    }

    @Test
    void getMyBookings_isUnauthorizedWithoutLogin() throws Exception {
        mockMvc.perform(get("/api/v1/bookings/me"))
            .andExpect(status().isUnauthorized());

        verifyNoInteractions(bookingService);
    }

    @Test
    void createBooking_isUnauthorizedWithoutLogin() throws Exception {
        mockMvc.perform(post("/api/v1/bookings")
                .contentType(MediaType.APPLICATION_JSON).content(body("Tea")))
            .andExpect(status().isUnauthorized());

        verifyNoInteractions(bookingService);
    }
}
