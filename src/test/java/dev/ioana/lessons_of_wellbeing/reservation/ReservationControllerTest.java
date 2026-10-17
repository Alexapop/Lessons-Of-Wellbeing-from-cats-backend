package dev.ioana.lessons_of_wellbeing.reservation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import dev.ioana.lessons_of_wellbeing.contracts.IReservationService;
import dev.ioana.lessons_of_wellbeing.reservation.dtos.ReservationDTORequest;
import dev.ioana.lessons_of_wellbeing.reservation.dtos.ReservationDTOResponse;
import dev.ioana.lessons_of_wellbeing.security.SecurityConfiguration;

@WebMvcTest(ReservationController.class)
@Import(SecurityConfiguration.class)
class ReservationControllerTest {
    @MockitoBean
    private org.springframework.security.oauth2.jwt.JwtDecoder jwtDecoder;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IReservationService reservationService;

    private static String body(String notes) {
        return "{\"name\":\"Elena Vance\",\"email\":\"elena@example.com\","
            + "\"phone\":\"+34 612 345 678\",\"notes\":\"" + notes + "\"}";
    }

    @Test
    void validateReservation_returnsDetailsWhenValid() throws Exception {
        when(reservationService.validateReservation(eq(2L), any(ReservationDTORequest.class)))
            .thenReturn(new ReservationDTOResponse(2L, "Kombucha Tea Tasting", "Elena Vance",
                "elena@example.com", "+34 612 345 678", "Hibiscus Rose"));

        mockMvc.perform(post("/api/v1/sessions/2/reservations").with(jwt())
                .contentType(MediaType.APPLICATION_JSON).content(body("Hibiscus Rose")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sessionId").value(2))
            .andExpect(jsonPath("$.notes").value("Hibiscus Rose"));
    }

    @Test
    void validateReservation_returnsBadRequestWithMessageWhenNotesAreMissing() throws Exception {
        when(reservationService.validateReservation(eq(2L), any(ReservationDTORequest.class)))
            .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, ReservationService.MISSING_NOTES_MESSAGE));

        mockMvc.perform(post("/api/v1/sessions/2/reservations").with(jwt())
                .contentType(MediaType.APPLICATION_JSON).content(body("")))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value(ReservationService.MISSING_NOTES_MESSAGE));
    }

    @Test
    void validateReservation_returnsBadRequestForInvalidContactDetails() throws Exception {
        mockMvc.perform(post("/api/v1/sessions/2/reservations").with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\",\"email\":\"not-an-email\",\"phone\":\"abc\",\"notes\":\"Tea\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void validateReservation_isUnauthorizedWithoutLogin() throws Exception {
        mockMvc.perform(post("/api/v1/sessions/2/reservations")
                .contentType(MediaType.APPLICATION_JSON).content(body("Tea")))
            .andExpect(status().isUnauthorized());
    }
}
