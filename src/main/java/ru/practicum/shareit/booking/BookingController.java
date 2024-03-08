package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private static final String header = "X-Sharer-User-Id";

    private final BookingService service;

    @PostMapping
    public BookingDtoResponse createBooking(@Valid @RequestBody BookingDtoRequest requestBooking,
                                            @RequestHeader(header) Long userId) {
        return BookingMapper.toResponse(service.createBooking(requestBooking, userId));
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse changeStatus(@PathVariable Long bookingId, @RequestHeader(header) Long userId,
                                           @RequestParam(name = "approved") Boolean approved) {
        return BookingMapper.toResponse(service.changeStatus(bookingId, userId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBooking(@PathVariable Long bookingId, @RequestHeader(header) Long userId) {
        return BookingMapper.toResponse(service.getBooking(bookingId, userId));
    }

    @GetMapping
    public List<BookingDtoResponse> getAllBookingsByUser(@RequestHeader(header) Long userId,
                                                         @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return service.getAllBookingsByUser(userId, state).stream()
                .map(BookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getAllBookingsItemsByOwner(@RequestHeader(header) Long userId,
                                                               @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return service.getAllBookingsItemsByOwner(userId, state).stream()
                .map(BookingMapper::toResponse)
                .collect(Collectors.toList());
    }
}
