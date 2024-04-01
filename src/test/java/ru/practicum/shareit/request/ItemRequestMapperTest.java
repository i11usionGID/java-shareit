package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemRequestMapperTest {

    private User user;
    private ItemRequestDto itemRequestDto;
    private ItemDto itemDto;

    private User requester;
    private ItemRequest itemRequest;
    private List<ItemDto> itemDtoList;



    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("mail@mail.ru")
                .name("Ivan")
                .build();
        itemRequestDto = ItemRequestDto.builder()
                .description("description")
                .requesterId(user.getId())
                .build();
        requester = User.builder()
                .id(2L)
                .email("newmail@mail.ru")
                .name("Anton")
                .build();
        itemDto = ItemDto.builder()
                .id(1L)
                .requestId(requester.getId())
                .name("ноутбук")
                .description("новый ноутбук")
                .available(true)
                .build();
        itemRequest = ItemRequest.builder()
                .created(LocalDateTime.now())
                .description("new request")
                .requester(requester)
                .build();
        itemDtoList = new ArrayList<>();
        itemDtoList.add(itemDto);
    }

    @Test
    void toRequest() {
        ItemRequest itemRequest = ItemRequestMapper.toRequest(itemRequestDto, user, LocalDateTime.now());
        assertEquals(user, itemRequest.getRequester());
        assertEquals("description", itemRequest.getDescription());
    }

    @Test
    void toResponse() {
        ItemRequestDtoResponse itemRequestDtoResponse = ItemRequestMapper.toResponse(itemRequest, itemDtoList);
        assertEquals(itemRequestDtoResponse.getItems(), itemDtoList);
        assertEquals(itemRequestDtoResponse.getDescription(), itemRequest.getDescription());
    }
}
