package ru.practicum.shareit.item;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long userId, Pageable pageable);

    List<Item> findAllByRequest(ItemRequest request);

    @Query("SELECT i from Item i WHERE upper(i.available) LIKE upper('true') AND (upper(i.name) LIKE upper(CONCAT('%', ?1, '%')) " +
            "OR upper(i.description) LIKE upper(CONCAT('%', ?1, '%')))")
    List<Item> searchWithPagination(String text, Pageable pageable);
}
