package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;


import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {
    private User user;
    private Item item;
    private ItemRequest itemRequest;

    private User owner;
    private BookingDtoRequest request;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("mail@mail.ru")
                .name("Ivan")
                .build();
        owner = User.builder()
                .id(2L)
                .email("newmail@mail.ru")
                .name("Anton")
                .build();
        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("new request")
                .created(LocalDateTime.now())
                .requester(new User())
                .build();
        item = Item.builder()
                .id(1L)
                .name("ноутбук")
                .description("мощный ноутбук")
                .available(true)
                .owner(owner)
                .request(itemRequest)
                .build();
        request = BookingDtoRequest.builder()
                .itemId(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .build();
        booking = BookingMapper.toBooking(request, user, item, Status.WAITING);
    }

    @Test
    void toBooking() {
        Booking booking1 = BookingMapper.toBooking(request, user, item, Status.WAITING);
        assertEquals(Status.WAITING, booking1.getStatus());
        assertEquals(user, booking1.getBooker());
        assertEquals(item, booking1.getItem());
    }

    @Test
    void toResponse() {
        BookingDtoResponse bookingDtoResp = BookingMapper.toResponse(booking);
        assertEquals(item.getName(), bookingDtoResp.getItem().getName());
        assertEquals(booking.getId(), bookingDtoResp.getId());
        assertEquals(booking.getStart(), bookingDtoResp.getStart());
    }

    @Test
    void toShort() {
        BookingShort bookingShort = BookingMapper.toShort(booking);
        assertEquals(booking.getId(), bookingShort.getId());
        assertEquals(booking.getBooker().getId(), bookingShort.getBookerId());
    }
}
