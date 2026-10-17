package dev.ioana.lessons_of_wellbeing.contracts;

import java.util.List;

import dev.ioana.lessons_of_wellbeing.booking.dtos.BookingDTORequest;
import dev.ioana.lessons_of_wellbeing.booking.dtos.BookingDTOResponse;
import dev.ioana.lessons_of_wellbeing.booking.dtos.UserBookingDTOResponse;

public interface IBookingService {
    BookingDTOResponse createBooking(String userSubject, BookingDTORequest request);

    List<UserBookingDTOResponse> getMyBookings(String userSubject);
}
