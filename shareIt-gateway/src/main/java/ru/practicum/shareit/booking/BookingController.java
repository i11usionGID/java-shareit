
package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@Validated
public class BookingController {
    private static final String USER_ID = "X-Sharer-User-Id";

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader(USER_ID) Long userId,
                                             @Valid @RequestBody BookingDtoRequest bookingDtoRequest) {
        return bookingClient.addBooking(userId, bookingDtoRequest);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> confirmationOrRejectionOfBooking(
            @PathVariable Long bookingId, @RequestHeader(USER_ID) Long userId, @RequestParam(name = "approved")
    Boolean approved) {
        return bookingClient.getStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@PathVariable Long bookingId, @RequestHeader(USER_ID) Long userId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsUser(@RequestHeader(USER_ID) Long userId,
                                                     @RequestParam(defaultValue = "ALL") String state,
                                                     @Valid @RequestParam(defaultValue = "1") @Min(1) Integer from,
                                                     @Valid @RequestParam(defaultValue = "20") @Min(1) @Max(20) Integer size) {
        return bookingClient.getUserBookings(userId, state, from, size, false);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllItemsUser(@RequestHeader(USER_ID) Long userId,
                                                  @RequestParam(defaultValue = "ALL") String state,
                                                  @Valid @RequestParam(defaultValue = "1") @Min(1) Integer from,
                                                  @Valid @RequestParam(defaultValue = "20") @Min(1) @Max(20) Integer size) {
        return bookingClient.getUserBookings(userId, state, from, size, true);
    }
}
