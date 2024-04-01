package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private ItemRepository itemRepository;
    private Item item;
    private User user;
    private User booker;
    private User booker1;
    private ItemRequest request;

    private Booking booking;
    private Booking booking1;

    @BeforeEach
    void setUp() {
        booker = User.builder()
                .email("new@mail.ru")
                .name("Ivan")
                .build();
        userRepository.save(booker);
        booker1 = User.builder()
                .email("old@mail.ru")
                .name("Anton")
                .build();
        userRepository.save(booker1);
        request = ItemRequest.builder()
                .requester(booker)
                .created(LocalDateTime.now())
                .description("description")
                .build();
        itemRequestRepository.save(request);
        user = User.builder()
                .email("newuser@mail.ru")
                .name("user")
                .build();
        userRepository.save(user);
        item = Item.builder()
                .name("ноутбук")
                .description("мощный ноутбук")
                .available(true)
                .owner(user)
                .request(request)
                .build();
        itemRepository.save(item);
        booking = Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .status(Status.WAITING)
                .build();
        bookingRepository.save(booking);
        booking1 = Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(3))
                .status(Status.WAITING)
                .build();
        bookingRepository.save(booking1);
    }

    @AfterEach
    void delete() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @Test
    void findAllByBookerOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookerOrderByStartDesc(booker, PageRequest.of(0, 5));
        assertFalse(bookings.isEmpty());
        assertEquals(booker, bookings.get(0).getBooker());
        assertEquals(2, bookings.size());
    }

    @Test
    void findFirstByBookerIdAndItemIdAndEndIsBeforeOrderByEndDesc() {
        Booking booking2 = bookingRepository.findFirstByBookerIdAndItemIdAndEndIsBeforeOrderByEndDesc(booker.getId(),
                item.getId(), LocalDateTime.now().plusDays(1));
        assertEquals(booking.getStart().getSecond(), booking2.getStart().getSecond());
    }

    @Test
    void findAllByItemOwnerAndStartIsBeforeAndEndIsAfterOrderByStart() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerAndStartIsBeforeAndEndIsAfterOrderByStart(user,
                LocalDateTime.now(), LocalDateTime.now().plusDays(2), PageRequest.of(0, 5));
        assertEquals(1, bookings.size());
        assertEquals(booking1.getStart().getSecond(), bookings.get(0).getStart().getSecond());
    }

    @Test
    void findAllByBookerAndEndIsBeforeOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookerAndEndIsBeforeOrderByStartDesc(booker,
                LocalDateTime.now().plusDays(5), PageRequest.of(0, 5));
        assertEquals(2, bookings.size());
    }

    @Test
    void findAllByBookerAndStatusEqualsOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookerAndStatusEqualsOrderByStartDesc(booker,
                Status.WAITING, PageRequest.of(0, 1));
        assertEquals(1, bookings.size());
        assertEquals(booking1.getStart().getSecond(), bookings.get(0).getStart().getSecond());
    }

    @Test
    void findAllByItemOwnerOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerOrderByStartDesc(user, PageRequest.of(0, 5));
        assertEquals(2, bookings.size());
    }

    @Test
    void findAllByBookerAndStartIsBeforeAndEndIsAfterOrderByStart() {
        List<Booking> bookings = bookingRepository.findAllByBookerAndStartIsBeforeAndEndIsAfterOrderByStart(booker,
                LocalDateTime.now(), LocalDateTime.now().plusDays(2), PageRequest.of(0, 5));
        assertEquals(1, bookings.size());
        assertFalse(bookings.contains(booking));
    }

    @Test
    void findAllByItemOwnerAndStartIsAfterOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerAndStartIsAfterOrderByStartDesc(user,
                LocalDateTime.now().minusHours(1), PageRequest.of(0, 1));
        assertEquals(1, bookings.size());
        assertEquals(booking1.getStart().getSecond(), bookings.get(0).getStart().getSecond());
    }

    @Test
    void findAllByItemOwnerAndEndIsBeforeOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerAndEndIsBeforeOrderByStartDesc(user,
                LocalDateTime.now().plusDays(4), PageRequest.of(0, 2));
        assertEquals(2, bookings.size());
    }

    @Test
    void findAllByItemOwnerAndStatusEqualsOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerAndStatusEqualsOrderByStartDesc(user,
                Status.WAITING, PageRequest.of(0, 1));
        assertEquals(1, bookings.size());
        assertEquals(booking1.getStart().getSecond(), bookings.get(0).getStart().getSecond());
    }

    @Test
    void findFirstByItemIdAndStartIsBeforeOrStartEqualsOrderByStartDesc() {
        Booking booking2 = bookingRepository.findFirstByItemIdAndStartIsBeforeOrStartEqualsOrderByStartDesc(item.getId(),
                LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        assertEquals(booking1.getStart().getSecond(), booking2.getStart().getSecond());
    }

    @Test
    void findAllByBookerAndStartIsAfterOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookerAndStartIsAfterOrderByStartDesc(booker,
                LocalDateTime.now().minusHours(5), PageRequest.of(0, 5));
        assertEquals(2, bookings.size());
    }

    @Test
    void findFirstByItemIdAndStartIsAfterOrderByStart() {
        Booking booking2 = bookingRepository.findFirstByItemIdAndStartIsAfterOrderByStart(item.getId(),
                LocalDateTime.now().minusHours(4));
        assertEquals(booking.getStart().getSecond(), booking2.getStart().getSecond());
    }
}
