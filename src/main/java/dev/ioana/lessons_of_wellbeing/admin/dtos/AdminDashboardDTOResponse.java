package dev.ioana.lessons_of_wellbeing.admin.dtos;

public record AdminDashboardDTOResponse(
    long totalSessions,
    long upcomingSessions,
    long confirmedReservations,
    long cancelledReservations,
    long registeredUsers
) {}
