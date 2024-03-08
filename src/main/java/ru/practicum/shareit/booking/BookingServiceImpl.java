package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    public Booking createBooking(BookingDtoRequest bookingDto, Long userId) {
        checkUser(userId);
        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new WrongDateException("Выставлена неверная дата, перепроверьте и введите еще раз.");
        }
        User user = userService.getUserById(userId);
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new DataNotFoundException("Предмета с таким id = " + bookingDto.getItemId() + " не существует."));
        if (userId == item.getOwner().getId())
            throw new SelfBookingException("Владелец не может забронировать вещь сам у себя.");
        if (!item.getAvailable()) {
            throw new UnavailableItemException("К сожалению, вещь недоступна для бронирования.");
        }
        return repository.save(BookingMapper.toBooking(bookingDto, user, item, Status.WAITING));
    }

    @Override
    public Booking changeStatus(Long bookingId, Long userId, Boolean approved) {
        checkUser(userId);
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new DataNotFoundException("Бронирования с таким id = " + bookingId + " не существует."));
        if (userId != booking.getItem().getOwner().getId()) {
            throw new WrongOwnerException("Статус бронирования может поменять только владелец.");
        }
        if (approved) {
            if (booking.getStatus() == Status.APPROVED) {
                throw new StatusAlreadyApprovedException("Статус бронирования уже подтвержден.");
            }
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        repository.save(booking);
        return booking;
    }

    @Override
    public Booking getBooking(Long id, Long userId) {
        checkUser(userId);
        Booking booking = repository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Бронирования с таким id = " + id + " не существует."));
        Item item = booking.getItem();
        if (userId != item.getOwner().getId() && userId != booking.getBooker().getId()) {
            throw new WrongOwnerException("Просматривать информацию могут только участники бронирования");
        }
        return booking;
    }

    @Override
    public List<Booking> getAllBookingsByUser(Long userId, String state) {
        checkUser(userId);
        LocalDateTime time = LocalDateTime.now();
        User user = userService.getUserById(userId);
        List<Booking> bookings;
        switch (state) {
            case "ALL":
                bookings = repository.findAllByBookerOrderByStartDesc(user);
                break;
            case "CURRENT":
                bookings = repository.findAllByBookerAndStartIsBeforeAndEndIsAfterOrderByStart(user, time, time);
                break;
            case "PAST":
                bookings = repository.findAllByBookerAndEndIsBeforeOrderByStartDesc(user, time);
                break;
            case "FUTURE":
                bookings = repository.findAllByBookerAndStartIsAfterOrderByStartDesc(user, time);
                break;
            case "WAITING":
                bookings = repository.findAllByBookerAndStatusEqualsOrderByStartDesc(user, Status.WAITING);
                break;
            case "REJECTED":
                bookings = repository.findAllByBookerAndStatusEqualsOrderByStartDesc(user, Status.REJECTED);
                break;
            default:
                throw new WrongDateException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings;
    }

    @Override
    public List<Booking> getAllBookingsItemsByOwner(Long ownerId, String state) {
        checkUser(ownerId);
        LocalDateTime time = LocalDateTime.now();
        User owner = userService.getUserById(ownerId);
        List<Booking> bookings;
        switch (state) {
            case "ALL":
                bookings = repository.findAllByItemOwnerOrderByStartDesc(owner);
                break;
            case "CURRENT":
                bookings = repository.findAllByItemOwnerAndStartIsBeforeAndEndIsAfterOrderByStart(owner, time, time);
                break;
            case "PAST":
                bookings = repository.findAllByItemOwnerAndEndIsBeforeOrderByStartDesc(owner, time);
                break;
            case "FUTURE":
                bookings = repository.findAllByItemOwnerAndStartIsAfterOrderByStartDesc(owner, time);
                break;
            case "WAITING":
                bookings = repository.findAllByItemOwnerAndStatusEqualsOrderByStartDesc(owner, Status.WAITING);
                break;
            case "REJECTED":
                bookings = repository.findAllByItemOwnerAndStatusEqualsOrderByStartDesc(owner, Status.REJECTED);
                break;
            default:
                throw new WrongDateException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings;
    }

    private void checkUser(Long userId) {
        try {
            userService.getUserById(userId);
        } catch (RuntimeException e) {
            throw new DataNotFoundException("Пользователя с таким id = " + userId + " не существует.");
        }
    }
}
