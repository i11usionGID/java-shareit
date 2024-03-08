package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static Booking toBooking(BookingDtoRequest bookingDto, User booker, Item item, Status status) {
        return Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .booker(booker)
                .item(item)
                .status(status)
                .build();
    }

    public static BookingDtoResponse toResponse(Booking booking) {
        UserDto owner = UserDto.builder()
                .id(booking.getBooker().getId())
                .build();
        ItemDto item = ItemDto.builder()
                .id(booking.getItem().getId())
                .name(booking.getItem().getName())
                .build();
        return BookingDtoResponse.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(owner)
                .item(item)
                .build();
    }

    public static BookingShort toShort(Booking booking) {
        return BookingShort.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}
