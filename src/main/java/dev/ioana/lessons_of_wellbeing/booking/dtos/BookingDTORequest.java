package dev.ioana.lessons_of_wellbeing.booking.dtos;

import dev.ioana.lessons_of_wellbeing.reservation.dtos.ReservationDTORequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record BookingDTORequest(
    @NotNull @Positive Long sessionId,
    @NotBlank @Size(max = 255) String name,
    @NotBlank @Email @Size(max = 255) String email,
    @NotBlank @Size(max = 20)
    @Pattern(regexp = "\\+?[0-9\\s().-]+") String phone,
    // Required only for Kombucha/Tea sessions, checked in ReservationService.
    @Size(max = 500) String notes
) {
    public ReservationDTORequest toReservationRequest() {
        return new ReservationDTORequest(name, email, phone, notes);
    }
}
