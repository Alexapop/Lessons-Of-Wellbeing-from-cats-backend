package dev.ioana.lessons_of_wellbeing.booking.dtos;

import java.time.LocalDateTime;

import dev.ioana.lessons_of_wellbeing.enums.ReservationStatus;

public record BookingDTOResponse(
    Long id,
    Long sessionId,
    String sessionTitle,
    LocalDateTime sessionStartDateTime,
    Long userId,
    ReservationStatus status,
    String notes,
    LocalDateTime createdAt
) {}
