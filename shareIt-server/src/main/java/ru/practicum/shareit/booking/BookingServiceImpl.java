package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public Booking createBooking(BookingDtoRequest bookingDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователя с таким id = " + userId + "  не существует"));
        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new WrongDateException("Выставлена неверная дата, перепроверьте и введите еще раз.");
        }
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new DataNotFoundException("Предмета с таким id = " + bookingDto.getItemId() + " не существует."));
        if (userId.equals(item.getOwner().getId()))
            throw new SelfBookingException("Владелец не может забронировать вещь сам у себя.");
        if (!item.getAvailable()) {
            throw new UnavailableItemException("К сожалению, вещь недоступна для бронирования.");
        }
        Booking booking = repository.save(BookingMapper.toBooking(bookingDto, user, item, Status.WAITING));
        return repository.findById(booking.getId())
                .orElseThrow(() -> new DataNotFoundException("Не найдено бронирование с  id " + booking.getId()));
    }

    @Override
    @Transactional
    public Booking changeStatus(Long bookingId, Long userId, Boolean approved) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователя с таким id = " + userId + "  не существует"));
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new DataNotFoundException("Бронирования с таким id = " + bookingId + " не существует."));
        if (!userId.equals(booking.getItem().getOwner().getId())) {
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователя с таким id = " + userId + "  не существует"));
        Booking booking = repository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Бронирования с таким id = " + id + " не существует."));
        Item item = booking.getItem();
        if (!userId.equals(item.getOwner().getId()) && !userId.equals(booking.getBooker().getId())) {
            throw new WrongOwnerException("Просматривать информацию могут только участники бронирования");
        }
        return booking;
    }

    @Override
    public List<Booking> getAllBookingsByUser(Long userId, String state, Integer from, Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователя с таким id = " + userId + "  не существует"));
        LocalDateTime time = LocalDateTime.now();
        Pageable pageable = PageRequest.of(from / size, size);
        List<Booking> bookings;
        switch (state) {
            case "ALL":
                bookings = repository.findAllByBookerOrderByStartDesc(user, pageable);
                break;
            case "CURRENT":
                bookings = repository.findAllByBookerAndStartIsBeforeAndEndIsAfterOrderByStart(user, time, time, pageable);
                break;
            case "PAST":
                bookings = repository.findAllByBookerAndEndIsBeforeOrderByStartDesc(user, time, pageable);
                break;
            case "FUTURE":
                bookings = repository.findAllByBookerAndStartIsAfterOrderByStartDesc(user, time, pageable);
                break;
            case "WAITING":
                bookings = repository.findAllByBookerAndStatusEqualsOrderByStartDesc(user, Status.WAITING, pageable);
                break;
            case "REJECTED":
                bookings = repository.findAllByBookerAndStatusEqualsOrderByStartDesc(user, Status.REJECTED, pageable);
                break;
            default:
                throw new WrongDateException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings;
    }

    @Override
    public List<Booking> getAllBookingsItemsByOwner(Long ownerId, String state, Integer from, Integer size) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new DataNotFoundException("Пользователя с таким id = " + ownerId + "  не существует"));
        LocalDateTime time = LocalDateTime.now();
        Pageable pageable = PageRequest.of(from / size, size);
        List<Booking> bookings;
        switch (state) {
            case "ALL":
                bookings = repository.findAllByItemOwnerOrderByStartDesc(owner, pageable);
                break;
            case "CURRENT":
                bookings = repository.findAllByItemOwnerAndStartIsBeforeAndEndIsAfterOrderByStart(owner, time, time, pageable);
                break;
            case "PAST":
                bookings = repository.findAllByItemOwnerAndEndIsBeforeOrderByStartDesc(owner, time, pageable);
                break;
            case "FUTURE":
                bookings = repository.findAllByItemOwnerAndStartIsAfterOrderByStartDesc(owner, time, pageable);
                break;
            case "WAITING":
                bookings = repository.findAllByItemOwnerAndStatusEqualsOrderByStartDesc(owner, Status.WAITING, pageable);
                break;
            case "REJECTED":
                bookings = repository.findAllByItemOwnerAndStatusEqualsOrderByStartDesc(owner, Status.REJECTED, pageable);
                break;
            default:
                throw new WrongDateException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings;
    }
}
