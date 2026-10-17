package dev.ioana.lessons_of_wellbeing.reservation.dtos;

public record ReservationDTOResponse(
    Long sessionId,
    String sessionTitle,
    String name,
    String email,
    String phone,
    String notes
) {}
