package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    Item createItem(ItemDto itemDto, Long userId);

    Item updateItem(ItemDto itemDto,Long userId,Long itemId);

    Item getItemById(Long itemId, Long userId);

    Collection<Item> getAllItemsByUser(Long userId);

    Collection<Item> getAllItemsByText(String text);
}
