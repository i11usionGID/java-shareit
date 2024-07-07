package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentDtoRequest;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndComments;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingController.HEADER;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService service;

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(HEADER) Long userId) {
        return ItemMapper.toDto(service.createItem(itemDto, userId));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@Valid @RequestBody CommentDtoRequest comment, @RequestHeader(HEADER) Long userId,
                                    @PathVariable(value = "itemId") Long itemId) {
        return CommentMapper.toDto(service.createComment(comment, userId, itemId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @RequestHeader(HEADER) Long userId,
                              @PathVariable(value = "itemId") Long itemId) {
        return ItemMapper.toDto(service.updateItem(itemDto, userId, itemId));
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingAndComments getItemById(@RequestHeader(HEADER) Long userId, @PathVariable(value = "itemId") Long itemId) {
        return service.getItemById(itemId, userId);
    }

    @GetMapping
    public Collection<ItemWithBookingAndComments> getAllItemsByUser(@RequestHeader(HEADER) Long userId,
                                                                    @RequestParam(defaultValue = "1") @Min(1) Integer from,
                                                                    @RequestParam(defaultValue = "20") @Min(1) @Max(20) Integer size) {
        return service.getAllItemsByUser(userId, from, size);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getAllItemsByText(@RequestParam(name = "text") String text,
                                                 @RequestParam(defaultValue = "1") @Min(1) Integer from,
                                                 @RequestParam(defaultValue = "20") @Min(1) @Max(20) Integer size) {
        return service.getAllItemsByText(text, from, size).stream()
                .map(s1 -> ItemMapper.toDto(s1))
                .collect(Collectors.toList());
    }
}
