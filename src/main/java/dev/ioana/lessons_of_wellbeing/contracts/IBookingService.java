package dev.ioana.lessons_of_wellbeing.contracts;

import dev.ioana.lessons_of_wellbeing.booking.dtos.BookingDTORequest;
import dev.ioana.lessons_of_wellbeing.booking.dtos.BookingDTOResponse;

public interface IBookingService {
    BookingDTOResponse createBooking(String userSubject, BookingDTORequest request);
}
