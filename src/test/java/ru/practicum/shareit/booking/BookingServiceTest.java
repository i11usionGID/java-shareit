package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;


import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceTest {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User user;
    private Item item;
    private ItemRequest itemRequest;

    private User owner;
    private BookingDtoRequest bookingDtoRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("new@mail.ru")
                .name("Ivan")
                .build();
        userRepository.save(user);
        owner = User.builder()
                .email("old@mail.ru")
                .name("Anton")
                .build();
        userRepository.save(owner);
        itemRequest = ItemRequest.builder()
                .description("new request")
                .created(LocalDateTime.now())
                .requester(user)
                .build();
        itemRequestRepository.save(itemRequest);
        item = Item.builder()
                .name("ноутбук")
                .description("новый ноутбук")
                .available(true)
                .owner(owner)
                .request(itemRequest)
                .build();
        itemRepository.save(item);
        bookingDtoRequest = BookingDtoRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .build();
    }

    @Test
    void createBooking() {
        Booking booking1 = bookingService.createBooking(bookingDtoRequest, user.getId());
        assertEquals(item.getName(), booking1.getItem().getName());
        assertEquals(owner, booking1.getItem().getOwner());
    }

    @Test
    void changeStatus() {
        Booking booking1 = bookingService.createBooking(bookingDtoRequest, user.getId());
        Booking bookingGetStatus = bookingService.changeStatus(booking1.getId(), owner.getId(), true);
        assertTrue(bookingGetStatus != null);
        assertEquals(bookingDtoRequest.getStart().getSecond(), bookingGetStatus.getStart().getSecond());
    }

    @Test
    void getBooking() {
        Booking addBooking = bookingService.createBooking(bookingDtoRequest, user.getId());
        Booking getBooking = bookingService.getBooking(addBooking.getId(), user.getId());
        assertTrue(getBooking != null);
        assertEquals(bookingDtoRequest.getStart().getSecond(), getBooking.getStart().getSecond());
    }

    @Test
    void getAllBookingsByUser() {
        Booking addBooking = bookingService.createBooking(bookingDtoRequest, user.getId());
        addBooking.setStatus(Status.APPROVED);
        bookingService.changeStatus(addBooking.getId(), owner.getId(), true);
        List<Booking> bookings = bookingService.getAllBookingsByUser(user.getId(), "ALL", 0, 5);
        assertEquals(1, bookings.size());
        assertEquals(Status.APPROVED, bookings.get(0).getStatus());
    }

    @Test
    void getAllBookingsItemsByOwner() {
        bookingService.createBooking(bookingDtoRequest, user.getId());
        List<Booking> bookings = bookingService.getAllBookingsItemsByOwner(owner.getId(), "ALL", 0, 5);
        assertEquals(1, bookings.size());
        assertEquals(owner, bookings.get(0).getItem().getOwner());
    }
}
