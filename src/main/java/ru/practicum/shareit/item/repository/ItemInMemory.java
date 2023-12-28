package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemInMemory implements ItemStorage {

    private Map<Long, Item> items = new HashMap<>();
    private Long id = 1L;

    @Override
    public Item create(Item item) {
        item.setId(id++);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        checkItemExist(item.getId());
        if (item.getName() != null) {
            items.get(item.getId()).setName(item.getName());
        }
        if (item.getDescription() != null) {
            items.get(item.getId()).setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            items.get(item.getId()).setAvailable(item.getAvailable());
        }
        return items.get(item.getId());
    }

    @Override
    public Item getById(Long itemId) {
        checkItemExist(itemId);
        return items.get(itemId);
    }

    @Override
    public Collection<Item> getAllByUser(Long userId) {
        return items.values().stream()
                .filter(p1 -> p1.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> getAllByText(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return items.values().stream()
                .filter(s1 -> s1.getName().toLowerCase().contains(text.toLowerCase()) ||
                        s1.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(s1 -> s1.getAvailable() == true)
                .collect(Collectors.toList());
    }

    private void checkItemExist(Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new DataNotFoundException("Item with this id not exist.");
        }
    }
}
