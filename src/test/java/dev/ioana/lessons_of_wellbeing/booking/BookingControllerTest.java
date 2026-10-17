package dev.ioana.lessons_of_wellbeing.booking;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

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
    void createBooking_isUnauthorizedWithoutLogin() throws Exception {
        mockMvc.perform(post("/api/v1/bookings")
                .contentType(MediaType.APPLICATION_JSON).content(body("Tea")))
            .andExpect(status().isUnauthorized());

        verifyNoInteractions(bookingService);
    }
}
