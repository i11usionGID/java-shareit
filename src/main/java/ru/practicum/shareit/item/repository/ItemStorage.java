package ru.practicum.shareit.item.repository;


import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {
    Item create(Item item);

    Item update(Item item);

    Item getById(Long itemId);

    Collection<Item> getAllByUser(Long userId);

    Collection<Item> getAllByText(String text);
}
