package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerOrderByStartDesc(User user);

    List<Booking> findAllByBookerAndStartIsBeforeAndEndIsAfterOrderByStart(User user, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerAndStartIsAfterOrderByStartDesc(User user, LocalDateTime start);

    List<Booking> findAllByBookerAndEndIsBeforeOrderByStartDesc(User user, LocalDateTime end);

    List<Booking> findAllByBookerAndStatusEqualsOrderByStartDesc(User user, Status status);

    List<Booking> findAllByItemOwnerOrderByStartDesc(User owner);

    List<Booking> findAllByItemOwnerAndStartIsBeforeAndEndIsAfterOrderByStart(User owner, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemOwnerAndStartIsAfterOrderByStartDesc(User owner, LocalDateTime start);

    List<Booking> findAllByItemOwnerAndEndIsBeforeOrderByStartDesc(User owner, LocalDateTime end);

    List<Booking> findAllByItemOwnerAndStatusEqualsOrderByStartDesc(User owner, Status status);

    Booking findFirstByBookerIdAndItemIdAndEndIsBeforeOrderByEndDesc(Long bookerId, Long itemId, LocalDateTime end);

    Booking findFirstByItemIdAndStartIsBeforeOrStartEqualsOrderByStartDesc(Long itemId, LocalDateTime start1, LocalDateTime start2);

    Booking findFirstByItemIdAndStartIsAfterOrderByStart(Long itemId, LocalDateTime start);
}
