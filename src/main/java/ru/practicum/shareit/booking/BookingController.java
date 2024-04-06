package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Validated
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    public static final String HEADER = "X-Sharer-User-Id";

    private final BookingService service;

    @PostMapping
    public BookingDtoResponse createBooking(@Valid @RequestBody BookingDtoRequest requestBooking,
                                            @RequestHeader(HEADER) Long userId) {
        return BookingMapper.toResponse(service.createBooking(requestBooking, userId));
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse changeStatus(@PathVariable Long bookingId, @RequestHeader(HEADER) Long userId,
                                           @RequestParam(name = "approved") Boolean approved) {
        return BookingMapper.toResponse(service.changeStatus(bookingId, userId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBooking(@PathVariable Long bookingId, @RequestHeader(HEADER) Long userId) {
        return BookingMapper.toResponse(service.getBooking(bookingId, userId));
    }

    @GetMapping
    public List<BookingDtoResponse> getAllBookingsByUser(@RequestHeader(HEADER) Long userId,
                                                         @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                         @RequestParam(defaultValue = "1") @Min(1) Integer from,
                                                         @RequestParam(defaultValue = "20") @Min(1) @Max(20) Integer size) {
        return service.getAllBookingsByUser(userId, state, from, size).stream()
                .map(BookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getAllBookingsItemsByOwner(@RequestHeader(HEADER) Long userId,
                                                               @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                               @RequestParam(defaultValue = "1") @Min(1) Integer from,
                                                               @RequestParam(defaultValue = "20") @Min(1) @Max(20) Integer size) {
        return service.getAllBookingsItemsByOwner(userId, state, from, size).stream()
                .map(BookingMapper::toResponse)
                .collect(Collectors.toList());
    }
}
