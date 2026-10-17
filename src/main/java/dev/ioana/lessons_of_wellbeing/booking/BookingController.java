package dev.ioana.lessons_of_wellbeing.booking;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.ioana.lessons_of_wellbeing.booking.dtos.BookingDTORequest;
import dev.ioana.lessons_of_wellbeing.booking.dtos.BookingDTOResponse;
import dev.ioana.lessons_of_wellbeing.booking.dtos.UserBookingDTOResponse;
import dev.ioana.lessons_of_wellbeing.contracts.IBookingService;
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "${api-endpoint}/bookings")
public class BookingController {
    private final IBookingService bookingService;

    public BookingController(IBookingService bookingService) {
        this.bookingService = bookingService;
    }

    // The booking is saved for the signed-in user taken from the JWT, never from the request body.
    @PostMapping
    public ResponseEntity<BookingDTOResponse> createBooking(
        @AuthenticationPrincipal Jwt jwt,
        @Valid @RequestBody BookingDTORequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(bookingService.createBooking(jwt.getSubject(), request));
    }

    // Returns only the bookings of the signed-in user taken from the JWT.
    @GetMapping("/me")
    public List<UserBookingDTOResponse> getMyBookings(@AuthenticationPrincipal Jwt jwt) {
        return bookingService.getMyBookings(jwt.getSubject());
    }

    // Cancels a booking of the signed-in user: the status becomes CANCELLED, the row is kept.
    @DeleteMapping("/{id}")
    public UserBookingDTOResponse cancelBooking(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return bookingService.cancelBooking(jwt.getSubject(), id);
    }
}
