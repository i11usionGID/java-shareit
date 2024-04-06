package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

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
                .email("new@mail.ru")
                .name("Ivan")
                .build();
        owner = User.builder()
                .id(2L)
                .email("oldmail@mail.ru")
                .name("Anton")
                .build();
        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("new request")
                .created(LocalDateTime.now())
                .requester(new User())
                .build();
        itemRequest.getRequester().setId(1L);
        item = Item.builder()
                .id(1L)
                .name("ноутбук")
                .description("удобный ноутбук")
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
        booking.setId(1L);
    }

    @Test
    void addBookingInputValueValid() {
        booking.setId(null);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(request.getItemId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        Booking saveBooking = bookingService.createBooking(request, user.getId());

        assertEquals(saveBooking.getItem(), item, "Метод работает некорректно");
        assertEquals(saveBooking.getBooker(), user, "Метод работает некорректно");
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createBookingBookerNotValidException() {
        when(userRepository.findById(anyLong())).thenThrow(DataNotFoundException.class);
        assertThrows(DataNotFoundException.class, () -> bookingService.createBooking(request, user.getId()));
    }

    @Test
    void createBookingItemNotValidException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenThrow(DataNotFoundException.class);
        assertThrows(DataNotFoundException.class, () -> bookingService.createBooking(request, user.getId()));
    }

    @Test
    void changeStatusAllInputValueValid() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        Booking actualStatusBooking = bookingService.changeStatus(booking.getId(), owner.getId(), true);
        assertEquals(Status.APPROVED, actualStatusBooking.getStatus(), "Метод отработал некорректно");
        assertEquals(item, actualStatusBooking.getItem(), "Метод отработал некорректно");
    }

    @Test
    void changeStatusUserNotValidException() {
        when(userRepository.findById(anyLong())).thenThrow(DataNotFoundException.class);
        assertThrows(DataNotFoundException.class, () -> bookingService.changeStatus(booking.getId(), user.getId(), true));
    }

    @Test
    void changeStatusBookingNotValidException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenThrow(DataNotFoundException.class);
        assertThrows(DataNotFoundException.class, () -> bookingService.changeStatus(booking.getId(), user.getId(), true));
    }

    @Test
    void changeStatusUserIdEqualsOwnerException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        assertThrows(DataNotFoundException.class, () -> bookingService.changeStatus(booking.getId(), user.getId(), true));
    }

    @Test
    void changeStatusBookingStatusEqualsApprovedException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        booking.setStatus(Status.APPROVED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertThrows(StatusAlreadyApprovedException.class, () -> bookingService.changeStatus(booking.getId(), owner.getId(), true));
    }

    @Test
    void changeStatusApprovedEqualsFalseReturnStatusRejected() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        Booking actualStatusBooking = bookingService.changeStatus(booking.getId(), owner.getId(), false);
        assertEquals(Status.REJECTED, actualStatusBooking.getStatus(), "Метод отработал некорректно");
        assertEquals(item, actualStatusBooking.getItem(), "Метод отработал некорректно");
    }

    @Test
    void getBookingInputValueValid() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        Booking booking1 = bookingService.getBooking(booking.getId(), user.getId());

        assertEquals(booking1.getId(), booking.getId(), "некорректная работа.");
        assertEquals(booking1.getBooker().getId(), user.getId(), "некорректная работа.");
    }

    @Test
    void getBookingUserIdDataNotFoundException() {
        when(userRepository.findById(anyLong())).thenThrow(DataNotFoundException.class);
        assertThrows(DataNotFoundException.class, () -> bookingService.getBooking(booking.getId(), user.getId()));
    }

    @Test
    void getBookingBookingIdDataNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenThrow(DataNotFoundException.class);
        assertThrows(DataNotFoundException.class, () -> bookingService.getBooking(booking.getId(), user.getId()));
    }

    @Test
    void getBookerIdEqualsOwnerIdException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertThrows(WrongOwnerException.class, () -> bookingService.getBooking(booking.getId(), 5L));
    }

    @Test
    void getAllBookingsByUserStatusEqualsAll() {
        Integer from = 1;
        Integer size = 10;
        LocalDateTime dateTime = LocalDateTime.now();
        Pageable pageable = PageRequest.of(from / size, size);
        List<Booking> list = List.of(booking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerOrderByStartDesc(user, pageable)).thenReturn(list);
        List<Booking> orderBooking = bookingService.getAllBookingsByUser(user.getId(), "ALL", from, size);
        assertFalse(orderBooking.isEmpty());
        assertEquals(1, orderBooking.size(), "некорректная работа.");
        verify(bookingRepository, never()).findAllByBookerAndStartIsBeforeAndEndIsAfterOrderByStart(user, dateTime,
                dateTime.plusDays(1), pageable);
    }

    @Test
    void getAllBookingsByUserStatusEqualsCURRENT() {
        Integer from = 1;
        Integer size = 10;
        LocalDateTime dateTime = LocalDateTime.now();
        Pageable pageable = PageRequest.of(from / size, size);
        List<Booking> list = List.of(booking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerAndStartIsBeforeAndEndIsAfterOrderByStart(any(User.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(list);
        List<Booking> orderBooking = bookingService.getAllBookingsByUser(user.getId(), "CURRENT", from, size);
        assertFalse(orderBooking.isEmpty());
        assertEquals(1, orderBooking.size(), "некорректная работа.");
        verify(bookingRepository, never()).findAllByBookerOrderByStartDesc(any(User.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByBookerAndStartIsBeforeAndEndIsAfterOrderByStart(any(User.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllBookingsByUserStatusEqualsFUTURE() {
        List<Booking> list = List.of(booking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerAndStartIsAfterOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class))).thenReturn(list);
        List<Booking> orderBooking = bookingService.getAllBookingsByUser(user.getId(), "FUTURE", 1, 1);
        assertFalse(orderBooking.isEmpty());
        assertEquals(1, orderBooking.size(), "некорректная работа.");
        verify(bookingRepository, never()).findAllByBookerOrderByStartDesc(any(User.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByBookerAndStartIsBeforeAndEndIsAfterOrderByStart(any(User.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByBookerAndStartIsAfterOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllBookingsByUserStatusEqualsPAST() {
        List<Booking> list = List.of(booking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerAndEndIsBeforeOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class))).thenReturn(list);
        List<Booking> orderBooking = bookingService.getAllBookingsByUser(user.getId(), "PAST", 1, 1);
        assertFalse(orderBooking.isEmpty());
        assertEquals(1, orderBooking.size(), "некорректная работа.");
        verify(bookingRepository, never()).findAllByBookerOrderByStartDesc(any(User.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByBookerAndStartIsBeforeAndEndIsAfterOrderByStart(any(User.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByBookerAndStartIsAfterOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllBookingsByUserStatusEqualsWAITING() {
        List<Booking> list = List.of(booking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerAndStatusEqualsOrderByStartDesc(any(User.class),
                any(Status.class), any(Pageable.class))).thenReturn(list);
        List<Booking> orderBooking = bookingService.getAllBookingsByUser(user.getId(), "WAITING", 1, 1);
        assertFalse(orderBooking.isEmpty());
        assertEquals(1, orderBooking.size(), "некорректная работа.");
        verify(bookingRepository, never()).findAllByBookerOrderByStartDesc(any(User.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByBookerAndStartIsBeforeAndEndIsAfterOrderByStart(any(User.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByBookerAndStartIsAfterOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByBookerAndEndIsBeforeOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllBookingsByUserStatusEqualsREJECTED() {
        List<Booking> list = List.of(booking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerAndStatusEqualsOrderByStartDesc(any(User.class),
                any(Status.class), any(Pageable.class))).thenReturn(list);
        List<Booking> orderBooking = bookingService.getAllBookingsByUser(user.getId(), "REJECTED", 1, 1);
        assertFalse(orderBooking.isEmpty());
        assertEquals(1, orderBooking.size(), "некорректная работа.");
        verify(bookingRepository, never()).findAllByBookerOrderByStartDesc(any(User.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByBookerAndStartIsBeforeAndEndIsAfterOrderByStart(any(User.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByBookerAndStartIsAfterOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByBookerAndEndIsBeforeOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByBookerAndStatusEqualsOrderByStartDesc(any(User.class),
                any(Status.class), any(Pageable.class));
    }

    @Test
    void getAllBookingsByUserStatusNotValidException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        assertThrows(WrongDateException.class, () -> bookingService.getAllBookingsByUser(user.getId(), "RRR", 1, 1));
    }

    @Test
    void getAllBookingsItemsByOwnerStatusEqualsALL() {
        List<Booking> list = List.of(booking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerOrderByStartDesc(any(User.class), any(Pageable.class)))
                .thenReturn(list);
        List<Booking> bookings = bookingService.getAllBookingsItemsByOwner(user.getId(), "ALL", 1, 1);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size(), "некорректная работа.");
        verify(bookingRepository, times(1))
                .findAllByItemOwnerOrderByStartDesc(any(User.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndStartIsBeforeAndEndIsAfterOrderByStart(any(User.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndStartIsAfterOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndEndIsBeforeOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndStatusEqualsOrderByStartDesc(any(User.class),
                any(Status.class), any(Pageable.class));
    }

    @Test
    void getAllBookingsItemsByOwnerStatusEqualsCURRENT() {
        List<Booking> list = List.of(booking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerAndStartIsBeforeAndEndIsAfterOrderByStart(any(User.class), any(LocalDateTime.class),
                any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(list);
        List<Booking> bookings = bookingService.getAllBookingsItemsByOwner(user.getId(), "CURRENT", 1, 1);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size(), "некорректная работа.");
        verify(bookingRepository, never())
                .findAllByItemOwnerOrderByStartDesc(any(User.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByItemOwnerAndStartIsBeforeAndEndIsAfterOrderByStart(any(User.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndStartIsAfterOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndEndIsBeforeOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndStatusEqualsOrderByStartDesc(any(User.class),
                any(Status.class), any(Pageable.class));
    }

    @Test
    void getAllBookingsItemsByOwnerStatusEqualsFUTURE() {
        List<Booking> list = List.of(booking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerAndStartIsAfterOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(list);
        List<Booking> bookings = bookingService.getAllBookingsItemsByOwner(user.getId(), "FUTURE", 1, 1);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size(), "некорректная работа.");
        verify(bookingRepository, never())
                .findAllByItemOwnerOrderByStartDesc(any(User.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndStartIsBeforeAndEndIsAfterOrderByStart(any(User.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByItemOwnerAndStartIsAfterOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndEndIsBeforeOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndStatusEqualsOrderByStartDesc(any(User.class),
                any(Status.class), any(Pageable.class));
    }

    @Test
    void getAllBookingsItemsByOwnerStatusEqualsPAST() {
        List<Booking> list = List.of(booking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerAndEndIsBeforeOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(list);
        List<Booking> bookings = bookingService.getAllBookingsItemsByOwner(user.getId(), "PAST", 1, 1);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size(), "некорректная работа.");
        verify(bookingRepository, never())
                .findAllByItemOwnerOrderByStartDesc(any(User.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndStartIsBeforeAndEndIsAfterOrderByStart(any(User.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndStartIsAfterOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByItemOwnerAndEndIsBeforeOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndStatusEqualsOrderByStartDesc(any(User.class),
                any(Status.class), any(Pageable.class));
    }

    @Test
    void getAllBookingsItemsByOwnerStatusEqualsWAITING() {
        List<Booking> list = List.of(booking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerAndStatusEqualsOrderByStartDesc(any(User.class),
                any(Status.class), any(Pageable.class)))
                .thenReturn(list);
        List<Booking> bookings = bookingService.getAllBookingsItemsByOwner(user.getId(), "WAITING", 1, 1);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size(), "некорректная работа.");
        verify(bookingRepository, never())
                .findAllByItemOwnerOrderByStartDesc(any(User.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndStartIsBeforeAndEndIsAfterOrderByStart(any(User.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndStartIsAfterOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndEndIsBeforeOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByItemOwnerAndStatusEqualsOrderByStartDesc(any(User.class),
                any(Status.class), any(Pageable.class));
    }

    @Test
    void getAllBookingsItemsByOwnerStatusEqualsREJECTED() {
        List<Booking> list = List.of(booking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerAndStatusEqualsOrderByStartDesc(any(User.class),
                any(Status.class), any(Pageable.class)))
                .thenReturn(list);
        List<Booking> bookings = bookingService.getAllBookingsItemsByOwner(user.getId(), "REJECTED", 1, 1);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size(), "некорректная работа.");
        verify(bookingRepository, never())
                .findAllByItemOwnerOrderByStartDesc(any(User.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndStartIsBeforeAndEndIsAfterOrderByStart(any(User.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndStartIsAfterOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerAndEndIsBeforeOrderByStartDesc(any(User.class),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByItemOwnerAndStatusEqualsOrderByStartDesc(any(User.class),
                any(Status.class), any(Pageable.class));
    }

    @Test
    void getAllBookingsItemsByOwnerStatusNotValidException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        assertThrows(WrongDateException.class, () -> bookingService.getAllBookingsItemsByOwner(user.getId(),
                "AAA", 1, 1));
    }
}