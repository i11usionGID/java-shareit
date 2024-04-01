package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerOrderByStartDesc(User user, Pageable pageable);

    List<Booking> findAllByBookerAndStartIsBeforeAndEndIsAfterOrderByStart(User user, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerAndStartIsAfterOrderByStartDesc(User user, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByBookerAndEndIsBeforeOrderByStartDesc(User user, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerAndStatusEqualsOrderByStartDesc(User user, Status status, Pageable pageable);

    List<Booking> findAllByItemOwnerOrderByStartDesc(User owner, Pageable pageable);

    List<Booking> findAllByItemOwnerAndStartIsBeforeAndEndIsAfterOrderByStart(User owner, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItemOwnerAndStartIsAfterOrderByStartDesc(User owner, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByItemOwnerAndEndIsBeforeOrderByStartDesc(User owner, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItemOwnerAndStatusEqualsOrderByStartDesc(User owner, Status status, Pageable pageable);

    Booking findFirstByBookerIdAndItemIdAndEndIsBeforeOrderByEndDesc(Long bookerId, Long itemId, LocalDateTime end);

    Booking findFirstByItemIdAndStartIsBeforeOrStartEqualsOrderByStartDesc(Long itemId, LocalDateTime start1, LocalDateTime start2);

    Booking findFirstByItemIdAndStartIsAfterOrderByStart(Long itemId, LocalDateTime start);
}
