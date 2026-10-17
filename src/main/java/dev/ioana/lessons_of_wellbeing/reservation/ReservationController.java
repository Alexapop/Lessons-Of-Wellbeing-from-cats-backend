package dev.ioana.lessons_of_wellbeing.reservation;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.ioana.lessons_of_wellbeing.contracts.IReservationService;
import dev.ioana.lessons_of_wellbeing.reservation.dtos.ReservationDTORequest;
import dev.ioana.lessons_of_wellbeing.reservation.dtos.ReservationDTOResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "${api-endpoint}/sessions/{id}/reservations")
public class ReservationController {
    private final IReservationService reservationService;

    public ReservationController(IReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // Validates the booking details only; reservations are not stored yet.
    @PostMapping
    public ReservationDTOResponse validateReservation(
        @PathVariable Long id,
        @Valid @RequestBody ReservationDTORequest request
    ) {
        return reservationService.validateReservation(id, request);
    }
}
