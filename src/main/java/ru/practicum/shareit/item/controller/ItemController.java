package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

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

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @RequestHeader(header) Long userId,
                              @PathVariable(value = "itemId") Long itemId) {
        return ItemMapper.toDto(service.updateItem(itemDto, userId, itemId));
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader(header) Long userId, @PathVariable(value = "itemId") Long itemId) {
        return ItemMapper.toDto(service.getItemById(itemId, userId));
    }

    @GetMapping
    public Collection<ItemDto> getAllItemsByUser(@RequestHeader(header) Long userId) {
        return service.getAllItemsByUser(userId).stream()
                .map(s1 -> ItemMapper.toDto(s1))
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public Collection<ItemDto> getAllItemsByText(@RequestParam(name = "text") String text) {
        return service.getAllItemsByText(text).stream()
                .map(s1 -> ItemMapper.toDto(s1))
                .collect(Collectors.toList());
    }
}
