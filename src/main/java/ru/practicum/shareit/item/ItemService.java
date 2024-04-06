package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndComments;

import java.util.Collection;

public interface ItemService {
    Item createItem(ItemDto itemDto, Long userId);

    Item updateItem(ItemDto itemDto, Long userId, Long itemId);

    ItemWithBookingAndComments getItemById(Long itemId, Long userId);

    Collection<ItemWithBookingAndComments> getAllItemsByUser(Long userId, Integer from, Integer size);

    Collection<Item> getAllItemsByText(String text, Integer from, Integer size);

    Comment createComment(CommentDtoRequest comment, Long authorId, Long itemId);
}
