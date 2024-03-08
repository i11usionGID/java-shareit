package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentDtoRequest;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndComments;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private static final String header = "X-Sharer-User-Id";

    private final ItemService service;

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(header) Long userId) {
        return ItemMapper.toDto(service.createItem(itemDto, userId));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@Valid @RequestBody CommentDtoRequest comment, @RequestHeader(header) Long userId,
                                    @PathVariable(value = "itemId") Long itemId) {
        return CommentMapper.toDto(service.createComment(comment, userId, itemId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @RequestHeader(header) Long userId,
                              @PathVariable(value = "itemId") Long itemId) {
        return ItemMapper.toDto(service.updateItem(itemDto, userId, itemId));
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingAndComments getItemById(@RequestHeader(header) Long userId, @PathVariable(value = "itemId") Long itemId) {
        return service.getItemById(itemId, userId);
    }

    @GetMapping
    public Collection<ItemWithBookingAndComments> getAllItemsByUser(@RequestHeader(header) Long userId) {
        return service.getAllItemsByUser(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getAllItemsByText(@RequestParam(name = "text") String text) {
        return service.getAllItemsByText(text).stream()
                .map(s1 -> ItemMapper.toDto(s1))
                .collect(Collectors.toList());
    }
}
