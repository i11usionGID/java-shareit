package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;

import java.util.List;

public interface BookingService {
    Booking createBooking(BookingDtoRequest bookingDto, Long userId);

    Booking changeStatus(Long bookingId, Long userId, Boolean approved);

    Booking getBooking(Long id, Long userId);

    List<Booking> getAllBookingsByUser(Long userId, String state, Integer from, Integer size);

    List<Booking> getAllBookingsItemsByOwner(Long ownerId, String state, Integer from, Integer size);
}
