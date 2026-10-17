package dev.ioana.lessons_of_wellbeing.contracts;

import dev.ioana.lessons_of_wellbeing.reservation.dtos.ReservationDTORequest;
import dev.ioana.lessons_of_wellbeing.reservation.dtos.ReservationDTOResponse;

public interface IReservationService {
    ReservationDTOResponse validateReservation(Long classId, ReservationDTORequest request);
}
