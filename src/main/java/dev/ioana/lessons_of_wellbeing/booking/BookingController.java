package dev.ioana.lessons_of_wellbeing.booking;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.ioana.lessons_of_wellbeing.booking.dtos.BookingDTORequest;
import dev.ioana.lessons_of_wellbeing.booking.dtos.BookingDTOResponse;
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
}
