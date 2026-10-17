package dev.ioana.lessons_of_wellbeing.reservation.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ReservationDTORequest(
    @NotBlank @Size(max = 255) String name,
    @NotBlank @Email @Size(max = 255) String email,
    @NotBlank @Size(max = 20)
    @Pattern(regexp = "\\+?[0-9\\s().-]+") String phone,
    // Required only for Kombucha/Tea sessions, checked in ReservationService.
    @Size(max = 500) String notes
) {}
