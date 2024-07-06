package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndComments;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {
    private Item item;
    private User user;
    private ItemRequest itemRequest;

    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("description")
                .created(LocalDateTime.now())
                .requester(new User())
                .build();
        item = Item.builder()
                .id(1L)
                .name("ноутбук")
                .description("новый ноутбук")
                .available(true)
                .owner(user)
                .request(itemRequest)
                .build();
        user = User.builder()
                .id(1L)
                .email("new@yandex.ru")
                .name("new")
                .build();
        itemDto = ItemDto.builder()
                .id(1L)
                .name("мышка")
                .description("игровая мышка")
                .available(true)
                .requestId(1L)
                .build();
    }

    @Test
    void toItem() {
        Item item1 = ItemMapper.toItem(itemDto, user, itemRequest);
        assertEquals(user, item1.getOwner(), "некорректная работа.");
    }

    @Test
    void toDto() {
        ItemDto itemDto = ItemMapper.toDto(item);
        assertEquals(itemDto.getRequestId(), item.getRequest().getId(), "некорректная работа.");
    }

    @Test
    void toItemWithId() {
        Item item1 = ItemMapper.toItemWithId(itemDto, user, item.getId());
        assertEquals(item1.getName(), itemDto.getName(), "некорректная работа.");
    }

    @Test
    void toItemWithBAndC() {
        List<CommentDto> list = new ArrayList<>();
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("text")
                .authorName("Ivan")
                .created(LocalDateTime.now())
                .build();
        list.add(commentDto);
        BookingShort last = BookingShort.builder()
                .id(1L)
                .bookerId(1L).build();
        BookingShort next = BookingShort.builder()
                .id(2L).bookerId(2L).build();
        ItemWithBookingAndComments item1 = ItemMapper.toItemWithBAndC(item, last, next, list);
        assertEquals(item1.getDescription(), item.getDescription(), "некорректная работа.");
        assertEquals(item1.getLastBooking(), last, "некорректная работа.");
        assertEquals(item1.getComments().get(0), commentDto, "некорректная работа.");
    }
}
