package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequesterOrderByCreatedDesc(User user);

    List<ItemRequest> findAllByRequesterNotOrderByCreatedDesc(User user, Pageable pageable);
}
