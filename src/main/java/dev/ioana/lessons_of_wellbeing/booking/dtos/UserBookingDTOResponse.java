package dev.ioana.lessons_of_wellbeing.booking.dtos;

import java.time.LocalDateTime;

import dev.ioana.lessons_of_wellbeing.classes.dtos.ClassDTOResponse;
import dev.ioana.lessons_of_wellbeing.enums.ReservationStatus;

public record UserBookingDTOResponse(
    Long id,
    ReservationStatus status,
    String notes,
    LocalDateTime createdAt,
    ClassDTOResponse session
) {}
