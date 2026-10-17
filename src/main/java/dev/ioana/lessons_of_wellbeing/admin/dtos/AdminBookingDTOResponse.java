package dev.ioana.lessons_of_wellbeing.admin.dtos;

import java.time.LocalDateTime;

import dev.ioana.lessons_of_wellbeing.classes.dtos.ClassDTOResponse;
import dev.ioana.lessons_of_wellbeing.enums.ReservationStatus;

public record AdminBookingDTOResponse(
    Long id,
    ReservationStatus status,
    String notes,
    LocalDateTime createdAt,
    BookingUserResponse user,
    ClassDTOResponse session
) {

    public record BookingUserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber
    ) {
    }
}
